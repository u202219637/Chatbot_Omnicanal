package pe.edu.upc.shadowchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.entities.*;
import pe.edu.upc.shadowchat.serviceInterfaces.*;

import java.util.List;

@RestController
@RequestMapping("/webhook")
public class TwilioWebhookController {

    @Autowired private IUsuarioCanalService usuarioCanalService;
    @Autowired private IConversacionService conversacionService;
    @Autowired private IMensajeService mensajeService;
    @Autowired private ICanalService canalService;
    @Autowired private IRagService ragService;
    @Autowired private ITwilioService twilioService;
    @Autowired private IEscalacionService escalacionService;

    @PostMapping(value = "/whatsapp",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> recibirMensaje(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        try {
            String numero = from.replace("whatsapp:", "").trim();
            if (!numero.startsWith("+")) numero = "+" + numero;

            final UsuarioCanal[] ucHolder = {null};
            final Usuario[] usuarioHolder = {null};

            try {
                ucHolder[0] = usuarioCanalService.findByIdentificador("WHATSAPP", numero);
                usuarioHolder[0] = ucHolder[0].getUsuario();
            } catch (Exception ex) {
                String numeroSinPlus = numero.replace("+51", "").trim();

                usuarioHolder[0] = usuarioCanalService.findUsuarioByTelefono(numero, numeroSinPlus);

                if (usuarioHolder[0] == null) {
                    throw new RuntimeException("Usuario no registrado");
                }

                Canal canalWA = canalService.findByNombre("WHATSAPP")
                        .orElseThrow(() -> new RuntimeException("Canal WA no existe"));

                UsuarioCanal nuevoUc = new UsuarioCanal();
                nuevoUc.setUsuario(usuarioHolder[0]);
                nuevoUc.setCanal(canalWA);
                nuevoUc.setIdentificadorExterno(numero);
                nuevoUc.setNombreExterno(
                        usuarioHolder[0].getNombres() + " " + usuarioHolder[0].getApellidos()
                );
                nuevoUc.setActivo(true);
                nuevoUc.setFechaVinculacion(java.time.LocalDateTime.now());

                ucHolder[0] = usuarioCanalService.save(nuevoUc);
            }

            UsuarioCanal uc = ucHolder[0];
            Usuario usuario = usuarioHolder[0];

            Conversacion conv = conversacionService
                    .findActivaOEscaladaByUsuario(usuario.getId())
                    .orElseGet(() -> {
                        Conversacion nueva = new Conversacion();
                        nueva.setUsuario(usuarioHolder[0]);
                        nueva.setOrigen("WHATSAPP");
                        nueva.setEstado("ABIERTA");
                        conversacionService.insert(nueva);
                        return nueva;
                    });

            Canal canal = canalService.findByNombre("WHATSAPP")
                    .orElseThrow(() -> new RuntimeException("Canal WHATSAPP no existe"));

            // Guarda mensaje del cliente
            Mensaje msgCliente = new Mensaje();
            msgCliente.setConversacion(conv);
            msgCliente.setUsuarioCanal(uc);
            msgCliente.setCanal(canal);
            msgCliente.setTipoEmisor("CLIENTE");
            msgCliente.setContenido(body);
            mensajeService.insert(msgCliente);

            // Detección de escalación desde WhatsApp
            boolean esEscalacion = ragService.preguntaDirecta(
                    "Eres un clasificador. Responde SOLO 'SI' si el cliente pide explícitamente hablar con un asesor, agente o persona humana. En CUALQUIER otro caso responde 'NO'.",
                    body
            ).trim().toUpperCase().startsWith("SI");
            if (esEscalacion && !"ESCALADA".equals(conv.getEstado())) {
                try {
                    escalacionService.crear(conv.getId(),
                            "Solicitud via WhatsApp: " + body, "MEDIA");
                } catch (Exception ignored) {}
                conv.setCantidadMensajes(
                        (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 1);
                conversacionService.update(conv);
                twilioService.enviarWhatsApp(numero,
                        "Entendido, te conectamos con un asesor. Un momento por favor.");
                return ResponseEntity.ok(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>");
            }

            // Si la conversación está escalada, no responder con IA
                    if ("ESCALADA".equals(conv.getEstado())) {
                        long mensajesCliente = mensajeService
                                .countByConversacionIdAndTipoEmisor(conv.getId(), "CLIENTE");
                        conv.setCantidadMensajes(
                                (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 1);
                        conversacionService.update(conv);
                        if (mensajesCliente <= 1) {
                            twilioService.enviarWhatsApp(numero,
                                    "Tu mensaje fue recibido. El asesor que te atiende lo verá pronto.");
                        }
                        return ResponseEntity.ok(
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>");
                    }

            // Flujo RAG normal
            String respuestaBot;
            try {
                Mensaje msgBotTemp = new Mensaje();
                msgBotTemp.setConversacion(conv);
                msgBotTemp.setCanal(canal);
                msgBotTemp.setTipoEmisor("BOT");
                msgBotTemp.setContenido("...");
                mensajeService.insert(msgBotTemp);
                respuestaBot = ragService.responder(conv, body, msgBotTemp.getId());
                msgBotTemp.setContenido(respuestaBot);
                mensajeService.insert(msgBotTemp);
            } catch (Exception e) {
                e.printStackTrace();
                respuestaBot = "Lo siento, hubo un error procesando tu consulta. Por favor intenta de nuevo.";
            }

            // Actualiza métricas
            conv.setCantidadMensajes(
                    (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 2);
            conversacionService.update(conv);

            // Envía respuesta por WhatsApp
            twilioService.enviarWhatsApp(numero, respuestaBot);

            return ResponseEntity.ok(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>");

        } catch (Exception e) {
            e.printStackTrace();
            String twiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<Response><Message>Hola! Para usar ShadowChat por WhatsApp, " +
                    "primero vincula tu número en nuestro portal web: " +
                    "shadowbyte.com → Mi Perfil → Vincular WhatsApp.</Message></Response>";
            return ResponseEntity.ok(twiml);
        }
    }
}