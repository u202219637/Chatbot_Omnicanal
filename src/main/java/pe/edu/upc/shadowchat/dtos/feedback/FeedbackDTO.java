package pe.edu.upc.shadowchat.dtos.feedback;
import java.time.LocalDateTime;
public class FeedbackDTO {
    private Long id;
    private Long conversacionId;
    private Integer calificacion;
    private String motivo;
    private String comentario;
    private LocalDateTime fechaRegistro;
    public Long getId()                              { return id; }
    public void setId(Long v)                        { this.id = v; }
    public Long getConversacionId()                  { return conversacionId; }
    public void setConversacionId(Long v)            { this.conversacionId = v; }
    public Integer getCalificacion()                 { return calificacion; }
    public void setCalificacion(Integer v)           { this.calificacion = v; }
    public String getMotivo()                        { return motivo; }
    public void setMotivo(String v)                  { this.motivo = v; }
    public String getComentario()                    { return comentario; }
    public void setComentario(String v)              { this.comentario = v; }
    public LocalDateTime getFechaRegistro()          { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime v)    { this.fechaRegistro = v; }
}