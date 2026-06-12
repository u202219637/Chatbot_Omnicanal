package pe.edu.upc.shadowchat.dtos.conversacion;
import java.time.LocalDateTime;

public class ConversacionListDTO {
    private Long id;
    private String asunto;
    private String estado;
    private String origen;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer cantidadMensajes;
    private Boolean fueResuelta;
    private Integer satisfaccion;
    private String clienteNombre;   // NUEVO — para vista admin
    private String clienteUsername; // NUEVO — para vista admin

    public Long getId()                              { return id; }
    public void setId(Long v)                        { this.id = v; }
    public String getAsunto()                        { return asunto; }
    public void setAsunto(String v)                  { this.asunto = v; }
    public String getEstado()                        { return estado; }
    public void setEstado(String v)                  { this.estado = v; }
    public String getOrigen()                        { return origen; }
    public void setOrigen(String v)                  { this.origen = v; }
    public LocalDateTime getFechaInicio()            { return fechaInicio; }
    public void setFechaInicio(LocalDateTime v)      { this.fechaInicio = v; }
    public LocalDateTime getFechaFin()               { return fechaFin; }
    public void setFechaFin(LocalDateTime v)         { this.fechaFin = v; }
    public Integer getCantidadMensajes()             { return cantidadMensajes; }
    public void setCantidadMensajes(Integer v)       { this.cantidadMensajes = v; }
    public Boolean getFueResuelta()                  { return fueResuelta; }
    public void setFueResuelta(Boolean v)            { this.fueResuelta = v; }
    public Integer getSatisfaccion()                 { return satisfaccion; }
    public void setSatisfaccion(Integer v)           { this.satisfaccion = v; }
    public String getClienteNombre()                 { return clienteNombre; }
    public void setClienteNombre(String v)           { this.clienteNombre = v; }
    public String getClienteUsername()               { return clienteUsername; }
    public void setClienteUsername(String v)         { this.clienteUsername = v; }
}