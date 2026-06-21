package pe.edu.upc.shadowchat.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.dtos.conversacion.*;
import pe.edu.upc.shadowchat.dtos.feedback.FeedbackDTO;
import pe.edu.upc.shadowchat.dtos.fuenterespuesta.FuenteRespuestaDTO;
import pe.edu.upc.shadowchat.entities.*;
import pe.edu.upc.shadowchat.serviceInterfaces.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    @Autowired private IEscalacionService escalacionService;
    @Autowired private IUsuarioCanalService usuarioCanalService;
    @Autowired private ITwilioService twilioService;

    // ── Palabras clave que disparan escalación automática ─────────────────────
    private static final List<String> PALABRAS_ESCALACION = List.of(
            "quiero un asesor", "hablar con un humano", "hablar con una persona",
            "quiero hablar con alguien", "necesito ayuda humana", "agente humano",
            "quiero un agente", "escalar", "soporte humano", "no me ayuda",
            "hablar con soporte", "asesor humano", "persona real"
    );

    private boolean detectaEscalacion(String texto) {
        try {
            return ragService.preguntaDirecta(
                    "Eres un clasificador. Responde SOLO 'SI' si el cliente pide explícitamente hablar con un asesor, agente o persona humana. En CUALQUIER otro caso responde 'NO'.",
                    texto
            ).trim().toUpperCase().startsWith("SI");
        } catch (Exception e) {
            String lower = texto.toLowerCase();
            return List.of("asesor", "agente", "humano", "persona real")
                    .stream().anyMatch(lower::contains);
        }
    }

    // POST /chat/mensaje (HU13, HU14, HU17, HU22)
    @PostMapping("/mensaje")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ADMINISTRADOR') or hasAuthority('ASESOR')")
    public ResponseEntity<MensajeResponseDTO> enviarMensaje(
            @RequestBody MensajeRequestDTO request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);
        String origen   = request.getOrigen() != null ? request.getOrigen() : "WEB";

        // OMNICANALIDAD: busca conversación activa sin filtrar por canal (HU20)
        Conversacion conv = conversacionService.findActivaOEscaladaByUsuario(usuario.getId())
                .orElseGet(() -> {
                    Conversacion nueva = new Conversacion();
                    nueva.setUsuario(usuario);
                    nueva.setOrigen(origen);
                    nueva.setEstado("ABIERTA");
                    conversacionService.insert(nueva);
                    return nueva;
                });

        Canal canal = canalService.findByNombre(origen)
                .orElseThrow(() -> new RuntimeException("Canal no existe: " + origen));

        long t0 = System.currentTimeMillis();

        // Guardar mensaje del cliente
        Mensaje msgCliente = new Mensaje();
        msgCliente.setConversacion(conv);
        msgCliente.setTipoEmisor("CLIENTE");
        msgCliente.setContenido(request.getContenido());
        msgCliente.setCanal(canal);
        mensajeService.insert(msgCliente);

        // ── Detección automática de escalación ───────────────────────────────
        boolean escalada = false;
        String respuesta;

        if (detectaEscalacion(request.getContenido())) {
            // Solo escalar si la conversación no está ya escalada
            if (!"ESCALADA".equals(conv.getEstado())) {
                try {
                    escalacionService.crear(conv.getId(),
                            "Solicitud del cliente: " + request.getContenido(), "MEDIA");
                    escalada = true;
                } catch (Exception ignored) {}
            }
            respuesta = escalada
                    ? "Entendido, te conecto con uno de nuestros asesores. " +
                      "Un momento por favor, ya estamos asignando tu caso. 🧑‍💼"
                    : "Ya tienes una solicitud de atención activa. " +
                      "Un asesor te atenderá pronto. 🧑‍💼";
        } else {
            // Si la conversación está escalada, guardar mensaje pero NO llamar al RAG
            if ("ESCALADA".equals(conv.getEstado())) {
                conv.setCantidadMensajes(
                        (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 1);
                conversacionService.update(conv);

                MensajeResponseDTO response = new MensajeResponseDTO();
                response.setConversacionId(conv.getId());
                response.setTipoEmisor("SISTEMA");
                response.setContenido("Mensaje enviado. El asesor lo verá pronto.");
                response.setEscalada(true);
                return ResponseEntity.ok(response);
            }
            // Flujo RAG normal
            Mensaje msgBot = new Mensaje();
            msgBot.setConversacion(conv);
            msgBot.setTipoEmisor("BOT");
            msgBot.setContenido("...");
            msgBot.setCanal(canal);
            mensajeService.insert(msgBot);

            try {
                respuesta = ragService.responder(conv, request.getContenido(), msgBot.getId());
            } catch (Exception e) {
                e.printStackTrace();
                respuesta = "Lo siento, hubo un error procesando tu consulta. Intenta de nuevo.";
            }

            long tiempoMs = System.currentTimeMillis() - t0;
            msgBot.setContenido(respuesta);
            mensajeService.insert(msgBot);

            conv.setCantidadMensajes(
                    (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 2);
            conv.setTiempoPromedioRespuestaMs((int) tiempoMs);
            conversacionService.update(conv);

            MensajeResponseDTO response = new MensajeResponseDTO();
            response.setConversacionId(conv.getId());
            response.setTipoEmisor("BOT");
            response.setContenido(respuesta);
            response.setEscalada(false);
            return ResponseEntity.ok(response);
        }

        // Guardar mensaje bot de escalación
        Mensaje msgEscalacion = new Mensaje();
        msgEscalacion.setConversacion(conv);
        msgEscalacion.setTipoEmisor("BOT");
        msgEscalacion.setContenido(respuesta);
        msgEscalacion.setCanal(canal);
        mensajeService.insert(msgEscalacion);

        conv.setCantidadMensajes(
                (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 2);
        conversacionService.update(conv);

        MensajeResponseDTO response = new MensajeResponseDTO();
        response.setConversacionId(conv.getId());
        response.setTipoEmisor("BOT");
        response.setContenido(respuesta);
        response.setEscalada(escalada);
        return ResponseEntity.ok(response);
    }

    // GET /chat/{conversacionId}/mensajes (HU13, HU18)
    @GetMapping("/{conversacionId}/mensajes")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public List<MensajeResponseDTO> mensajes(@PathVariable Long conversacionId) {
        List<Mensaje> listaMensajes = mensajeService.listByConversacion(conversacionId);

        // FIX rendimiento: antes se llamaba fuenteRespuestaService.listByMensaje(id)
        // DENTRO del .map() de cada mensaje -> 1 query SQL extra por cada mensaje
        // (N+1). Con conversaciones de 10-20+ mensajes, sumado a que este endpoint
        // se llama cada 5s por el polling del chat (cliente y panel de asesor),
        // esto saturaba el plan B1 y hacía que el polling se sintiera "congelado"
        // (la respuesta tardaba más que el propio intervalo de 5s entre polls).
        // Ahora se trae TODO en una sola query y se agrupa en memoria por mensaje.
        List<Long> idsMensajes = listaMensajes.stream().map(Mensaje::getId).collect(Collectors.toList());
        Map<Long, List<FuenteRespuesta>> fuentesPorMensaje = fuenteRespuestaService
                .listByMensajes(idsMensajes).stream()
                .collect(Collectors.groupingBy(fr -> fr.getMensaje().getId()));

        return listaMensajes.stream().map(m -> {
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
            if (m.getCanal() != null) d.setCanalNombre(m.getCanal().getNombre());

            List<FuenteRespuestaDTO> fuentes = fuentesPorMensaje
                    .getOrDefault(m.getId(), List.of()).stream().map(fr -> {
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

    // GET /chat/historial — cliente ve sus propias conversaciones (HU18)
    @GetMapping("/historial")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public List<ConversacionListDTO> historial() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioService.findByUsername(username);
        return toListDTO(conversacionService.listByUsuario(u.getId()));
    }

    // GET /chat/admin/conversaciones — admin/asesor ven TODAS con filtros (HU22, HU25)
    @GetMapping("/admin/conversaciones")
    @PreAuthorize("hasAuthority('ADMINISTRADOR') or hasAuthority('ASESOR')")
    public List<ConversacionListDTO> todasConversaciones(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDateTime desdeDateTime = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaDateTime = hasta != null ? hasta.plusDays(1).atStartOfDay() : null;
        return toListDTO(conversacionService.listAll(estado, origen, desdeDateTime, hastaDateTime));
    }

    // PUT /chat/{conversacionId}/cerrar
    @PutMapping("/{conversacionId}/cerrar")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ASESOR')")
    public ResponseEntity<Void> cerrar(@PathVariable Long conversacionId,
                                       @RequestParam(defaultValue = "true") Boolean resuelta) {
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
    // POST /chat/{conversacionId}/mensaje-asesor (HU22 — asesor responde al cliente)
    @PostMapping("/{conversacionId}/mensaje-asesor")
    @PreAuthorize("hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponseDTO> mensajeAsesor(
            @PathVariable Long conversacionId,
            @RequestBody MensajeRequestDTO request) {

        Conversacion conv = conversacionService.searchId(conversacionId);
        if (conv == null)
            return ResponseEntity.notFound().build();

        String origen = conv.getOrigen() != null ? conv.getOrigen() : "WEB";
        Canal canal = canalService.findByNombre(origen)
                .orElseGet(() -> canalService.findByNombre("WEB")
                        .orElseThrow(() -> new RuntimeException("Canal WEB no encontrado")));

        Mensaje msg = new Mensaje();
        msg.setConversacion(conv);
        String asesorUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario asesorUser = usuarioService.findByUsername(asesorUsername);
        String nombreAsesor = asesorUser.getNombres() + " " + asesorUser.getApellidos();
        msg.setTipoEmisor("ASESOR");
        msg.setContenido(request.getContenido());
        msg.setCanal(canal);
        mensajeService.insert(msg);

        conv.setCantidadMensajes(
                (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 1);
        conversacionService.update(conv);

// Enviar por WhatsApp si la conversación tiene canal WhatsApp
        try {
            conv.getUsuario().getId();
            pe.edu.upc.shadowchat.entities.UsuarioCanal ucWA =
                    usuarioCanalService.listByUsuario(conv.getUsuario().getId())
                            .stream()
                            .filter(uc -> "WHATSAPP".equals(uc.getCanal().getNombre()))
                            .findFirst().orElse(null);
            if (ucWA != null) {
                twilioService.enviarWhatsApp(
                        ucWA.getIdentificadorExterno(),
                        "[Asesor " + nombreAsesor + "]: " + request.getContenido()                );
            }
        } catch (Exception ignored) {}

        MensajeResponseDTO response = new MensajeResponseDTO();
        response.setId(msg.getId());
        response.setConversacionId(conversacionId);
        response.setTipoEmisor("ASESOR");
        response.setContenido(msg.getContenido());
        response.setFechaEnvio(msg.getFechaEnvio());
        response.setEscalada(false);
        return ResponseEntity.ok(response);
    }
    private List<ConversacionListDTO> toListDTO(List<Conversacion> list) {
        return list.stream().map(c -> {
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
            if (c.getUsuario() != null) {
                d.setClienteNombre(c.getUsuario().getNombres() + " " + c.getUsuario().getApellidos());
                d.setClienteUsername(c.getUsuario().getUsername());
            }
            return d;
        }).collect(Collectors.toList());
    }
}