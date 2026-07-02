package pe.edu.upc.shadowchat.dtos.conversacion;
public class MensajeRequestDTO {
    private String contenido;
    private String origen; // WEB | WHATSAPP
    private String mediaUrl;

    public String getContenido()         { return contenido; }
    public void setContenido(String v)   { this.contenido = v; }
    public String getOrigen()            { return origen; }
    public void setOrigen(String v)      { this.origen = v; }
    public String getMediaUrl()          { return mediaUrl; }
    public void setMediaUrl(String v)    { this.mediaUrl = v; }
}