package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "escalacion")
public class Escalacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_conversacion", nullable = false)
    @JsonIgnore
    private Conversacion conversacion;

    // Nullable hasta que se asigna un asesor
    @ManyToOne
    @JoinColumn(name = "id_asesor", nullable = true)
    private Usuario asesor;

    @Column(name = "motivo", length = 300)
    private String motivo;

    // BAJA, MEDIA, ALTA, URGENTE
    @Column(name = "prioridad", length = 10)
    private String prioridad = "MEDIA";

    // PENDIENTE, ASIGNADA, EN_ATENCION, RESUELTA, CERRADA
    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    public Escalacion() {}

    public Escalacion(Long id, Conversacion conversacion, Usuario asesor,
                      String motivo, String prioridad, String estado) {
        this.id = id;
        this.conversacion = conversacion;
        this.asesor = asesor;
        this.motivo = motivo;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Conversacion getConversacion() { return conversacion; }
    public void setConversacion(Conversacion conversacion) { this.conversacion = conversacion; }

    public Usuario getAsesor() { return asesor; }
    public void setAsesor(Usuario asesor) { this.asesor = asesor; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
}