package pe.edu.upc.shadowchat.dtos.dashboard;
public class KpiDTO {
    private Long totalConversaciones;
    private Long conversacionesResueltas;
    private Long totalMensajes;
    private String tiempoPromedioRespuesta;
    private Double tasaResolucionAutomatica;
    private Double mensajesPorConversacion;
    private Double satisfaccionPromedio;
    private Double tasaDesvioHumano;
    private Long conversacionesWeb;
    private Long conversacionesWhatsapp;
    public Long getTotalConversaciones()                  { return totalConversaciones; }
    public void setTotalConversaciones(Long v)            { this.totalConversaciones = v; }
    public Long getConversacionesResueltas()              { return conversacionesResueltas; }
    public void setConversacionesResueltas(Long v)        { this.conversacionesResueltas = v; }
    public Long getTotalMensajes()                        { return totalMensajes; }
    public void setTotalMensajes(Long v)                  { this.totalMensajes = v; }
    public String getTiempoPromedioRespuesta()            { return tiempoPromedioRespuesta; }
    public void setTiempoPromedioRespuesta(String v)      { this.tiempoPromedioRespuesta = v; }
    public Double getTasaResolucionAutomatica()           { return tasaResolucionAutomatica; }
    public void setTasaResolucionAutomatica(Double v)     { this.tasaResolucionAutomatica = v; }
    public Double getMensajesPorConversacion()            { return mensajesPorConversacion; }
    public void setMensajesPorConversacion(Double v)      { this.mensajesPorConversacion = v; }
    public Double getSatisfaccionPromedio()               { return satisfaccionPromedio; }
    public void setSatisfaccionPromedio(Double v)         { this.satisfaccionPromedio = v; }
    public Double getTasaDesvioHumano()                   { return tasaDesvioHumano; }
    public void setTasaDesvioHumano(Double v)             { this.tasaDesvioHumano = v; }
    public Long getConversacionesWeb()                    { return conversacionesWeb; }
    public void setConversacionesWeb(Long v)              { this.conversacionesWeb = v; }
    public Long getConversacionesWhatsapp()               { return conversacionesWhatsapp; }
    public void setConversacionesWhatsapp(Long v)         { this.conversacionesWhatsapp = v; }
}