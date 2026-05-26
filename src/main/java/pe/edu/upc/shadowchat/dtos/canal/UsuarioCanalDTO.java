package pe.edu.upc.shadowchat.dtos.canal;
import java.time.LocalDateTime;
public class UsuarioCanalDTO {
    private Long id;
    private String canalNombre;
    private String identificadorExterno;
    private String nombreExterno;
    private Boolean activo;
    private LocalDateTime fechaVinculacion;
    public Long getId()                              { return id; }
    public void setId(Long v)                        { this.id = v; }
    public String getCanalNombre()                   { return canalNombre; }
    public void setCanalNombre(String v)             { this.canalNombre = v; }
    public String getIdentificadorExterno()          { return identificadorExterno; }
    public void setIdentificadorExterno(String v)    { this.identificadorExterno = v; }
    public String getNombreExterno()                 { return nombreExterno; }
    public void setNombreExterno(String v)           { this.nombreExterno = v; }
    public Boolean getActivo()                       { return activo; }
    public void setActivo(Boolean v)                 { this.activo = v; }
    public LocalDateTime getFechaVinculacion()       { return fechaVinculacion; }
    public void setFechaVinculacion(LocalDateTime v) { this.fechaVinculacion = v; }
}