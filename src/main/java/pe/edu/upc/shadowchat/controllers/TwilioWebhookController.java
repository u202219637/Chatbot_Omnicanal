package pe.edu.upc.shadowchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.entities.*;
import pe.edu.upc.shadowchat.serviceInterfaces.*;

@RestController
@RequestMapping("/webhook")
public class TwilioWebhookController {

    @Autowired private IUsuarioCanalService usuarioCanalService;
    @Autowired private IConversacionService conversacionService;
    @Autowired private IMensajeService mensajeService;
    @Autowired private ICanalService canalService;
    @Autowired private IRagService ragService;
    @Autowired private ITwilioService twilioService;

    @PostMapping(value = "/whatsapp",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> recibirMensaje(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        try {
            // "whatsapp:+51999999999" → "+51999999999"
            String numero = from.replace("whatsapp:", "").trim();
            // Twilio siempre manda +51xxxxxxxxx — esta línea es solo seguro extra
            if (!numero.startsWith("+")) numero = "+" + numero;

            // Resuelve el Usuario por número de WhatsApp vinculado
            UsuarioCanal uc = usuarioCanalService
                    .findByIdentificador("WHATSAPP", numero);
            Usuario usuario = uc.getUsuario();

            // Recupera conversación ABIERTA en canal WHATSAPP o crea nueva
            Conversacion conv = conversacionService
                    .findActiva(usuario.getId(), "WHATSAPP")
                    .orElseGet(() -> {
                        Conversacion nueva = new Conversacion();
                        nueva.setUsuario(usuario);
                        nueva.setOrigen("WHATSAPP");
                        nueva.setEstado("ABIERTA");
                        conversacionService.insert(nueva);
                        return nueva;
                    });

            // Guarda mensaje del cliente
            Canal canal = canalService.findByNombre("WHATSAPP")
                    .orElseThrow(() -> new RuntimeException("Canal WHATSAPP no existe"));

            Mensaje msgCliente = new Mensaje();
            msgCliente.setConversacion(conv);
            msgCliente.setUsuarioCanal(uc);
            msgCliente.setCanal(canal);
            msgCliente.setTipoEmisor("CLIENTE");
            msgCliente.setContenido(body);
            mensajeService.insert(msgCliente);

            // Crea mensaje bot placeholder para obtener ID
            Mensaje msgBot = new Mensaje();
            msgBot.setConversacion(conv);
            msgBot.setCanal(canal);
            msgBot.setTipoEmisor("BOT");
            msgBot.setContenido("...");
            mensajeService.insert(msgBot);

            // Llama al RAG (mismo pipeline que el chat web)
            String respuestaBot;
            try {
                respuestaBot = ragService.responder(conv, body, msgBot.getId());
            } catch (Exception e) {
                e.printStackTrace();
                respuestaBot = "Lo siento, hubo un error procesando tu consulta. Por favor intenta de nuevo.";
            }

            // Actualiza el mensaje bot con la respuesta real
            msgBot.setContenido(respuestaBot);
            mensajeService.insert(msgBot);

            // Actualiza métricas de la conversación
            conv.setCantidadMensajes(
                    (conv.getCantidadMensajes() != null ? conv.getCantidadMensajes() : 0) + 2
            );
            conversacionService.update(conv);

            // Envía respuesta al número de WhatsApp vía Twilio
            twilioService.enviarWhatsApp(numero, respuestaBot);

            // TwiML vacío — Twilio ya recibió el ack, la respuesta se envía por API
            return ResponseEntity.ok(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>"
            );

        } catch (Exception e) {
            // Número no vinculado → instrucciones de registro
            String twiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<Response><Message>Hola! Para usar ShadowChat por WhatsApp, " +
                    "primero vincula tu número en nuestro portal web: " +
                    "shadowbyte.com → Mi Perfil → Vincular WhatsApp.</Message></Response>";
            return ResponseEntity.ok(twiml);
        }
    }
}