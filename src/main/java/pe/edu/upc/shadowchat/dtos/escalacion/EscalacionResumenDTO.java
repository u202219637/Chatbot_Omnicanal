package pe.edu.upc.shadowchat.dtos.escalacion;
public class EscalacionResumenDTO {
    private Long total;
    private Long pendientes;
    private Long enRevision;
    private Long resueltas;
    private Long alta;
    private Long media;
    private Long baja;
    private String tiempoPromedioRespuesta;
    public Long getTotal()                               { return total; }
    public void setTotal(Long v)                         { this.total = v; }
    public Long getPendientes()                          { return pendientes; }
    public void setPendientes(Long v)                    { this.pendientes = v; }
    public Long getEnRevision()                          { return enRevision; }
    public void setEnRevision(Long v)                    { this.enRevision = v; }
    public Long getResueltas()                           { return resueltas; }
    public void setResueltas(Long v)                     { this.resueltas = v; }
    public Long getAlta()                                { return alta; }
    public void setAlta(Long v)                          { this.alta = v; }
    public Long getMedia()                               { return media; }
    public void setMedia(Long v)                         { this.media = v; }
    public Long getBaja()                                { return baja; }
    public void setBaja(Long v)                          { this.baja = v; }
    public String getTiempoPromedioRespuesta()           { return tiempoPromedioRespuesta; }
    public void setTiempoPromedioRespuesta(String v)     { this.tiempoPromedioRespuesta = v; }
}