package pe.edu.upc.shadowchat.serviceImplements;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.serviceInterfaces.ITwilioService;

@Service
public class TwilioServiceImplement implements ITwilioService {

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.whatsapp.number:whatsapp:+14155238886}")
    private String fromNumber;

    private boolean habilitado = false;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isBlank()
                && !accountSid.equals("PENDIENTE_REGISTRO")
                && authToken != null && !authToken.isBlank()
                && !authToken.equals("PENDIENTE_REGISTRO")) {
            Twilio.init(accountSid, authToken);
            habilitado = true;
            System.out.println("[TwilioService] Inicializado correctamente.");
        } else {
            System.out.println("[TwilioService] Credenciales no configuradas — WhatsApp deshabilitado.");
        }
    }

    @Override
    public void enviarWhatsApp(String numero, String mensaje) {
        if (!habilitado) {
            System.out.println("[TwilioService] SIMULADO → " + numero + ": " + mensaje);
            return;
        }
        try {
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + numero),
                    new PhoneNumber(fromNumber),
                    mensaje
            ).create();
            System.out.println("[TwilioService] Enviado SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("[TwilioService] Error enviando a " + numero + ": " + e.getMessage());
        }
    }
}