package pe.edu.upc.shadowchat.serviceInterfaces;

public interface ITwilioService {
    /**
     * Envía un mensaje de WhatsApp via Twilio.
     * @param numero  Número destino con formato internacional: +51999999999
     * @param mensaje Texto a enviar
     */
    void enviarWhatsApp(String numero, String mensaje);
}