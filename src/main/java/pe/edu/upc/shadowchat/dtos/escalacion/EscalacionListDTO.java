package pe.edu.upc.shadowchat.dtos.escalacion;
import java.time.LocalDateTime;
public class EscalacionListDTO {
    private Long id;
    private Long conversacionId;
    private String clienteNombre;
    private String clienteUsername;
    private String motivo;
    private String prioridad;
    private String estado;
    private String asesorNombre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaCierre;
    public Long getId()                                  { return id; }
    public void setId(Long v)                            { this.id = v; }
    public Long getConversacionId()                      { return conversacionId; }
    public void setConversacionId(Long v)                { this.conversacionId = v; }
    public String getClienteNombre()                     { return clienteNombre; }
    public void setClienteNombre(String v)               { this.clienteNombre = v; }
    public String getClienteUsername()                   { return clienteUsername; }
    public void setClienteUsername(String v)             { this.clienteUsername = v; }
    public String getMotivo()                            { return motivo; }
    public void setMotivo(String v)                      { this.motivo = v; }
    public String getPrioridad()                         { return prioridad; }
    public void setPrioridad(String v)                   { this.prioridad = v; }
    public String getEstado()                            { return estado; }
    public void setEstado(String v)                      { this.estado = v; }
    public String getAsesorNombre()                      { return asesorNombre; }
    public void setAsesorNombre(String v)                { this.asesorNombre = v; }
    public LocalDateTime getFechaCreacion()              { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime v)        { this.fechaCreacion = v; }
    public LocalDateTime getFechaAsignacion()            { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime v)      { this.fechaAsignacion = v; }
    public LocalDateTime getFechaCierre()                { return fechaCierre; }
    public void setFechaCierre(LocalDateTime v)          { this.fechaCierre = v; }
}