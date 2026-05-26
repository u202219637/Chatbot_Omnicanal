package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_conversacion", nullable = false)
    @JsonIgnore
    private Conversacion conversacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Del 1 al 5
    @Column(name = "calificacion")
    private Integer calificacion;

    // RESPUESTA_INCORRECTA, TARDO_DEMASIADO, BUENA_ATENCION, OTRO
    @Column(name = "motivo", length = 50)
    private String motivo;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public Feedback() {}

    public Feedback(Long id, Conversacion conversacion, Usuario usuario,
                    Integer calificacion, String motivo, String comentario) {
        this.id = id;
        this.conversacion = conversacion;
        this.usuario = usuario;
        this.calificacion = calificacion;
        this.motivo = motivo;
        this.comentario = comentario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Conversacion getConversacion() { return conversacion; }
    public void setConversacion(Conversacion conversacion) { this.conversacion = conversacion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}