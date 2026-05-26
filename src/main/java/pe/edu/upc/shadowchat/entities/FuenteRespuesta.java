package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "fuente_respuesta")
public class FuenteRespuesta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mensaje", nullable = false)
    @JsonIgnore
    private Mensaje mensaje;

    // Nullable: puede ser fragmento O producto (no ambos obligatoriamente)
    @ManyToOne
    @JoinColumn(name = "id_fragmento", nullable = true)
    private FragmentoConocimiento fragmento;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = true)
    private Producto producto;

    // FRAGMENTO_RAG, PRODUCTO, FAQ, MANUAL
    @Column(name = "tipo_fuente", length = 30)
    private String tipoFuente;

    // Score de similitud coseno (0.0 - 1.0)
    @Column(name = "score_relevancia", precision = 5, scale = 4)
    private BigDecimal scoreRelevancia;

    public FuenteRespuesta() {}

    public FuenteRespuesta(Long id, Mensaje mensaje, FragmentoConocimiento fragmento,
                           Producto producto, String tipoFuente, BigDecimal scoreRelevancia) {
        this.id = id;
        this.mensaje = mensaje;
        this.fragmento = fragmento;
        this.producto = producto;
        this.tipoFuente = tipoFuente;
        this.scoreRelevancia = scoreRelevancia;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Mensaje getMensaje() { return mensaje; }
    public void setMensaje(Mensaje mensaje) { this.mensaje = mensaje; }

    public FragmentoConocimiento getFragmento() { return fragmento; }
    public void setFragmento(FragmentoConocimiento fragmento) { this.fragmento = fragmento; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public String getTipoFuente() { return tipoFuente; }
    public void setTipoFuente(String tipoFuente) { this.tipoFuente = tipoFuente; }

    public BigDecimal getScoreRelevancia() { return scoreRelevancia; }
    public void setScoreRelevancia(BigDecimal scoreRelevancia) { this.scoreRelevancia = scoreRelevancia; }
}