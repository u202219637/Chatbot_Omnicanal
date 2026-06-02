package pe.edu.upc.shadowchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.entities.*;
import pe.edu.upc.shadowchat.serviceInterfaces.*;

/**
 * Recibe mensajes entrantes de WhatsApp vía Twilio webhook.
 * URL: POST /webhook/whatsapp  — permitAll() en WebSecurityConfig
 *
 * Twilio envía form-urlencoded con estos campos:
 *   From  = "whatsapp:+51999999999"
 *   Body  = texto del mensaje
 *   To    = número Twilio
 *
 * FLUJO OMNICANAL (HU20):
 *   1. Extrae el número del campo From
 *   2. Busca el UsuarioCanal con ese número → resuelve el Usuario
 *   3. Recupera conversación ABIERTA o crea una nueva
 *   4. Guarda el mensaje del cliente
 *   5. TODO: llama a RagService para respuesta del bot
 *   6. Responde con TwiML vacío (Twilio espera XML)
 */
@RestController
@RequestMapping("/webhook")
public class TwilioWebhookController {

    @Autowired private IUsuarioCanalService usuarioCanalService;
    @Autowired private IConversacionService conversacionService;
    @Autowired private IMensajeService mensajeService;
    @Autowired private ICanalService canalService;

    @PostMapping(value = "/whatsapp",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> recibirMensaje(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        try {
            // "whatsapp:+51999999999" → "+51999999999"
            String numero = from.replace("whatsapp:", "").trim();

            // Resuelve el Usuario por número de WhatsApp
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
            Mensaje msg = new Mensaje();
            msg.setConversacion(conv);
            msg.setUsuarioCanal(uc);
            msg.setCanal(canal);
            msg.setTipoEmisor("CLIENTE");
            msg.setContenido(body);
            mensajeService.insert(msg);

            // TODO Sprint 2: respuesta del bot via RagService + TwilioService
            // String respuestaBot = ragService.responder(conv, body);
            // twilioService.sendWhatsApp(numero, respuestaBot);

            // TwiML vacío — Twilio requiere XML como respuesta
            return ResponseEntity.ok("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>");

        } catch (Exception e) {
            // Si el número no está vinculado, responde con mensaje de registro
            String twiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<Response><Message>Hola! Para usar ShadowChat por WhatsApp, " +
                    "primero vincula tu número en nuestro portal web.</Message></Response>";
            return ResponseEntity.ok(twiml);
        }
    }
}