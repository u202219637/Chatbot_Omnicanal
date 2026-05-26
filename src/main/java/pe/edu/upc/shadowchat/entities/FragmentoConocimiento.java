package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "fragmento_conocimiento")
public class FragmentoConocimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_documento", nullable = false)
    @JsonIgnore
    private DocumentoConocimiento documentoConocimiento;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    /*
     * NOTA IMPORTANTE - pgvector:
     * Este campo almacena el vector de 1536 dimensiones generado por
     * text-embedding-ada-002 de OpenAI.
     *
     * ANTES de levantar Spring Boot por primera vez ejecutar en PostgreSQL:
     *   CREATE EXTENSION IF NOT EXISTS vector;
     *
     * El tipo VECTOR(1536) es reconocido por Hibernate gracias a la
     * dependencia pgvector en el pom.xml.
     *
     * Se mapea como float[] en Java y se convierte con el tipo personalizado.
     */
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private float[] embedding;

    @Column(name = "orden_fragmento")
    private Integer ordenFragmento;

    @Column(name = "cantidad_tokens")
    private Integer cantidadTokens;

    @Column(name = "estado")
    private Boolean estado = true;

    public FragmentoConocimiento() {}

    public FragmentoConocimiento(Long id, DocumentoConocimiento documentoConocimiento,
                                 String contenido, float[] embedding,
                                 Integer ordenFragmento, Integer cantidadTokens) {
        this.id = id;
        this.documentoConocimiento = documentoConocimiento;
        this.contenido = contenido;
        this.embedding = embedding;
        this.ordenFragmento = ordenFragmento;
        this.cantidadTokens = cantidadTokens;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DocumentoConocimiento getDocumentoConocimiento() { return documentoConocimiento; }
    public void setDocumentoConocimiento(DocumentoConocimiento d) { this.documentoConocimiento = d; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }

    public Integer getOrdenFragmento() { return ordenFragmento; }
    public void setOrdenFragmento(Integer ordenFragmento) { this.ordenFragmento = ordenFragmento; }

    public Integer getCantidadTokens() { return cantidadTokens; }
    public void setCantidadTokens(Integer cantidadTokens) { this.cantidadTokens = cantidadTokens; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}