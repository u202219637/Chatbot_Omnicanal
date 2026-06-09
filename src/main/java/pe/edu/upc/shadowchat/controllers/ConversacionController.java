package pe.edu.upc.shadowchat.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.dtos.conversacion.*;
import pe.edu.upc.shadowchat.dtos.feedback.FeedbackDTO;
import pe.edu.upc.shadowchat.dtos.fuenterespuesta.FuenteRespuestaDTO;
import pe.edu.upc.shadowchat.entities.*;
import pe.edu.upc.shadowchat.serviceInterfaces.*;
import pe.edu.upc.shadowchat.serviceInterfaces.IRagService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@SecurityRequirement(name = "bearerAuth")
public class ConversacionController {

    @Autowired private IConversacionService conversacionService;
    @Autowired private IMensajeService mensajeService;
    @Autowired private IFuenteRespuestaService fuenteRespuestaService;
    @Autowired private IFeedbackService feedbackService;
    @Autowired private IUsuarioService usuarioService;
    @Autowired private ICanalService canalService;
    @Autowired private IRagService ragService;

    // POST /chat/mensaje (HU13, HU14, HU17)
    @PostMapping("/mensaje")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<MensajeResponseDTO> enviarMensaje(
            @RequestBody MensajeRequestDTO request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);
        String origen = request.getOrigen() != null ? request.getOrigen() : "WEB";

        Conversacion conv = conversacionService.findActiva(usuario.getId(), origen)
                .orElseGet(() -> {
                    canalService.findByNombre(origen)
                            .orElseThrow(() -> new RuntimeException("Canal no existe: " + origen));
                    Conversacion nueva = new Conversacion();
                    nueva.setUsuario(usuario);
                    nueva.setOrigen(origen);
                    nueva.setEstado("ABIERTA");
                    conversacionService.insert(nueva);
                    return nueva;
                });

        Canal canal = canalService.findByNombre(origen)
                .orElseThrow(() -> new RuntimeException("Canal no existe"));

        // Guardar mensaje cliente
        long t0 = System.currentTimeMillis();
        Mensaje msgCliente = new Mensaje();
        msgCliente.setConversacion(conv);
        msgCliente.setTipoEmisor("CLIENTE");
        msgCliente.setContenido(request.getContenido());
        msgCliente.setCanal(canal);
        mensajeService.insert(msgCliente);

        // Crear mensaje bot con placeholder para obtener ID
        Mensaje msgBot = new Mensaje();
        msgBot.setConversacion(conv);
        msgBot.setTipoEmisor("BOT");
        msgBot.setContenido("...");
        msgBot.setCanal(canal);
        mensajeService.insert(msgBot);

        // Llamar RAG + OpenAI
        String respuesta;
        try {
            respuesta = ragService.responder(conv, request.getContenido(), msgBot.getId());
        } catch (Exception e) {
            e.printStackTrace();
            respuesta = "Lo siento, hubo un error procesando tu consulta. Intenta de nuevo.";
        }

        // Actualizar mensaje bot con respuesta real
        long tiempoMs = System.currentTimeMillis() - t0;
        msgBot.setContenido(respuesta);
        mensajeService.insert(msgBot);

        // Actualizar métricas conversación
        conv.setCantidadMensajes((conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 2);
        conv.setTiempoPromedioRespuestaMs((int) tiempoMs);
        conversacionService.update(conv);

        MensajeResponseDTO response = new MensajeResponseDTO();
        response.setId(msgBot.getId());
        response.setConversacionId(conv.getId());
        response.setTipoEmisor("BOT");
        response.setContenido(respuesta);
        response.setFechaEnvio(msgBot.getFechaEnvio());
        response.setEscalada(false);
        return ResponseEntity.ok(response);
    }

    // GET /chat/{conversacionId}/mensajes (HU13, HU18)
    @GetMapping("/{conversacionId}/mensajes")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public List<MensajeResponseDTO> mensajes(@PathVariable Long conversacionId) {
        return mensajeService.listByConversacion(conversacionId).stream().map(m -> {
            MensajeResponseDTO d = new MensajeResponseDTO();
            d.setId(m.getId());
            d.setConversacionId(conversacionId);
            d.setTipoEmisor(m.getTipoEmisor());
            d.setContenido(m.getContenido());
            d.setIntencionDetectada(m.getIntencionDetectada());
            d.setConfianzaIa(m.getConfianzaIa());
            d.setTokensEntrada(m.getTokensEntrada());
            d.setTokensSalida(m.getTokensSalida());
            d.setFechaEnvio(m.getFechaEnvio());
            // Fuentes RAG (HU16)
            List<FuenteRespuestaDTO> fuentes = fuenteRespuestaService
                    .listByMensaje(m.getId()).stream().map(fr -> {
                        FuenteRespuestaDTO fd = new FuenteRespuestaDTO();
                        fd.setId(fr.getId());
                        fd.setTipoFuente(fr.getTipoFuente());
                        fd.setScoreRelevancia(fr.getScoreRelevancia());
                        if (fr.getFragmento() != null && fr.getFragmento().getDocumentoConocimiento() != null)
                            fd.setTituloDocumento(fr.getFragmento().getDocumentoConocimiento().getTitulo());
                        if (fr.getFragmento() != null) {
                            String c = fr.getFragmento().getContenido();
                            fd.setExtractoContenido(c != null ? c.substring(0, Math.min(200, c.length())) : null);
                        }
                        return fd;
                    }).collect(Collectors.toList());
            d.setFuentes(fuentes);
            return d;
        }).collect(Collectors.toList());
    }

    // GET /chat/historial (HU18)
    @GetMapping("/historial")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public List<ConversacionListDTO> historial() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioService.findByUsername(username);
        return conversacionService.listByUsuario(u.getId()).stream().map(c -> {
            ConversacionListDTO d = new ConversacionListDTO();
            d.setId(c.getId());
            d.setAsunto(c.getAsunto());
            d.setEstado(c.getEstado());
            d.setOrigen(c.getOrigen());
            d.setFechaInicio(c.getFechaInicio());
            d.setFechaFin(c.getFechaFin());
            d.setCantidadMensajes(c.getCantidadMensajes());
            d.setFueResuelta(c.getFueResuelta());
            d.setSatisfaccion(c.getSatisfaccion());
            return d;
        }).collect(Collectors.toList());
    }

    // PUT /chat/{conversacionId}/cerrar
    @PutMapping("/{conversacionId}/cerrar")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ASESOR')")
    public ResponseEntity<Void> cerrar(@PathVariable Long conversacionId,
                                       @RequestParam(defaultValue="true") Boolean resuelta) {
        conversacionService.cerrar(conversacionId, resuelta);
        return ResponseEntity.ok().build();
    }

    // POST /chat/{conversacionId}/feedback (HU23)
    @PostMapping("/{conversacionId}/feedback")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<Void> calificar(@PathVariable Long conversacionId,
                                          @RequestBody FeedbackDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioService.findByUsername(username);
        Conversacion conv = conversacionService.searchId(conversacionId);
        Feedback fb = new Feedback();
        fb.setConversacion(conv);
        fb.setUsuario(u);
        fb.setCalificacion(dto.getCalificacion());
        fb.setMotivo(dto.getMotivo());
        fb.setComentario(dto.getComentario());
        feedbackService.insert(fb);
        return ResponseEntity.ok().build();
    }
}