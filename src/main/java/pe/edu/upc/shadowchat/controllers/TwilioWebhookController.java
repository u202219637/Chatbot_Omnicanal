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

            UsuarioCanal uc = usuarioCanalService
                    .findByIdentificador("WHATSAPP", numero);
            Usuario usuario = uc.getUsuario();

            Conversacion conv = conversacionService
                    .findActivaByUsuario(usuario.getId())
                    .orElseGet(() -> {
                        Conversacion nueva = new Conversacion();
                        nueva.setUsuario(usuario);
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
            List<String> palabrasEscalacion = List.of(
                    "quiero un asesor", "hablar con un humano", "asesor humano",
                    "quiero escalar", "escalar", "persona real", "soporte humano",
                    "agente humano", "quiero hablar con alguien"
            );
            String bodyLower = body.toLowerCase();
            boolean esEscalacion = palabrasEscalacion.stream().anyMatch(bodyLower::contains);

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
                conv.setCantidadMensajes(
                        (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 1);
                conversacionService.update(conv);
                twilioService.enviarWhatsApp(numero,
                        "Tu mensaje fue recibido. El asesor que te atiende lo verá pronto.");
                return ResponseEntity.ok(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>");
            }

            // Flujo RAG normal
            String respuestaBot;
            try {
                respuestaBot = ragService.responder(conv, body, null);
            } catch (Exception e) {
                e.printStackTrace();
                respuestaBot = "Lo siento, hubo un error procesando tu consulta. Por favor intenta de nuevo.";
            }

            // Guarda respuesta del bot
            Mensaje msgBot = new Mensaje();
            msgBot.setConversacion(conv);
            msgBot.setCanal(canal);
            msgBot.setTipoEmisor("BOT");
            msgBot.setContenido(respuestaBot);
            mensajeService.insert(msgBot);

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