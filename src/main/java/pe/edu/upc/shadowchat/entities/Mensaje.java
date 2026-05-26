package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mensaje")
public class Mensaje implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_conversacion", nullable = false)
    @JsonIgnore
    private Conversacion conversacion;

    @ManyToOne
    @JoinColumn(name = "id_canal", nullable = false)
    private Canal canal;

    // Nullable: los mensajes del BOT no tienen usuario_canal
    @ManyToOne
    @JoinColumn(name = "id_usuario_canal", nullable = true)
    private UsuarioCanal usuarioCanal;

    // CLIENTE, BOT, ASESOR, SISTEMA
    @Column(name = "tipo_emisor", nullable = false, length = 20)
    private String tipoEmisor;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    // Intencion detectada por el NLP (ej: CONSULTA_PRECIO, SOPORTE_TECNICO)
    @Column(name = "intencion_detectada", length = 100)
    private String intencionDetectada;

    // Nivel de confianza del modelo (0.0 - 1.0)
    @Column(name = "confianza_ia", precision = 5, scale = 4)
    private BigDecimal confianzaIa;

    // Tokens consumidos - para control de costos OpenAI
    @Column(name = "tokens_entrada")
    private Integer tokensEntrada;

    @Column(name = "tokens_salida")
    private Integer tokensSalida;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    // Trazabilidad RAG: que fragmentos uso el bot para responder
    @OneToMany(mappedBy = "mensaje", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FuenteRespuesta> fuentesRespuesta;

    public Mensaje() {}

    public Mensaje(Long id, Conversacion conversacion, Canal canal,
                   UsuarioCanal usuarioCanal, String tipoEmisor, String contenido) {
        this.id = id;
        this.conversacion = conversacion;
        this.canal = canal;
        this.usuarioCanal = usuarioCanal;
        this.tipoEmisor = tipoEmisor;
        this.contenido = contenido;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Conversacion getConversacion() { return conversacion; }
    public void setConversacion(Conversacion conversacion) { this.conversacion = conversacion; }

    public Canal getCanal() { return canal; }
    public void setCanal(Canal canal) { this.canal = canal; }

    public UsuarioCanal getUsuarioCanal() { return usuarioCanal; }
    public void setUsuarioCanal(UsuarioCanal usuarioCanal) { this.usuarioCanal = usuarioCanal; }

    public String getTipoEmisor() { return tipoEmisor; }
    public void setTipoEmisor(String tipoEmisor) { this.tipoEmisor = tipoEmisor; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getIntencionDetectada() { return intencionDetectada; }
    public void setIntencionDetectada(String intencionDetectada) { this.intencionDetectada = intencionDetectada; }

    public BigDecimal getConfianzaIa() { return confianzaIa; }
    public void setConfianzaIa(BigDecimal confianzaIa) { this.confianzaIa = confianzaIa; }

    public Integer getTokensEntrada() { return tokensEntrada; }
    public void setTokensEntrada(Integer tokensEntrada) { this.tokensEntrada = tokensEntrada; }

    public Integer getTokensSalida() { return tokensSalida; }
    public void setTokensSalida(Integer tokensSalida) { this.tokensSalida = tokensSalida; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public List<FuenteRespuesta> getFuentesRespuesta() { return fuentesRespuesta; }
    public void setFuentesRespuesta(List<FuenteRespuesta> fuentesRespuesta) { this.fuentesRespuesta = fuentesRespuesta; }
}