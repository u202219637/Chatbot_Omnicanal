package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversacion")
public class Conversacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "asunto", length = 300)
    private String asunto;

    // ABIERTA, RESUELTA, ESCALADA, CERRADA, ABANDONADA
    @Column(name = "estado", length = 20)
    private String estado = "ABIERTA";

    // WEB, WHATSAPP
    @Column(name = "origen", length = 20)
    private String origen;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio = LocalDateTime.now();

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "resumen", columnDefinition = "TEXT")
    private String resumen;

    // ============================================
    // Metricas integradas (antes MetricaConversacion)
    // ============================================

    @Column(name = "tiempo_primera_respuesta_ms")
    private Integer tiempoPrimeraRespuestaMs;

    @Column(name = "tiempo_promedio_respuesta_ms")
    private Integer tiempoPromedioRespuestaMs;

    @Column(name = "cantidad_mensajes")
    private Integer cantidadMensajes = 0;

    @Column(name = "fue_resuelta")
    private Boolean fueResuelta = false;

    @Column(name = "fue_escalada")
    private Boolean fueEscalada = false;

    // Del 1 al 5 - viene de Feedback cuando el usuario califica
    @Column(name = "satisfaccion")
    private Integer satisfaccion;

    // ============================================
    // Relaciones hijas
    // ============================================

    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Mensaje> mensajes;

    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Escalacion> escalaciones;

    public Conversacion() {}

    public Conversacion(Long id, Usuario usuario, String asunto, String estado,
                        String origen, LocalDateTime fechaInicio) {
        this.id = id;
        this.usuario = usuario;
        this.asunto = asunto;
        this.estado = estado;
        this.origen = origen;
        this.fechaInicio = fechaInicio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }

    public Integer getTiempoPrimeraRespuestaMs() { return tiempoPrimeraRespuestaMs; }
    public void setTiempoPrimeraRespuestaMs(Integer t) { this.tiempoPrimeraRespuestaMs = t; }

    public Integer getTiempoPromedioRespuestaMs() { return tiempoPromedioRespuestaMs; }
    public void setTiempoPromedioRespuestaMs(Integer t) { this.tiempoPromedioRespuestaMs = t; }

    public Integer getCantidadMensajes() { return cantidadMensajes; }
    public void setCantidadMensajes(Integer cantidadMensajes) { this.cantidadMensajes = cantidadMensajes; }

    public Boolean getFueResuelta() { return fueResuelta; }
    public void setFueResuelta(Boolean fueResuelta) { this.fueResuelta = fueResuelta; }

    public Boolean getFueEscalada() { return fueEscalada; }
    public void setFueEscalada(Boolean fueEscalada) { this.fueEscalada = fueEscalada; }

    public Integer getSatisfaccion() { return satisfaccion; }
    public void setSatisfaccion(Integer satisfaccion) { this.satisfaccion = satisfaccion; }

    public List<Mensaje> getMensajes() { return mensajes; }
    public void setMensajes(List<Mensaje> mensajes) { this.mensajes = mensajes; }

    public List<Feedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }

    public List<Escalacion> getEscalaciones() { return escalaciones; }
    public void setEscalaciones(List<Escalacion> escalaciones) { this.escalaciones = escalaciones; }
}