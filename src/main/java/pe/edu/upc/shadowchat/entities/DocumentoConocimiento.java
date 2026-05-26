package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documento_conocimiento")
public class DocumentoConocimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Admin que subio el documento
    @ManyToOne
    @JoinColumn(name = "id_usuario_carga", nullable = false)
    private Usuario usuarioCarga;

    // Nullable: no todos los documentos son de un producto especifico
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = true)
    private Producto producto;

    @Column(name = "titulo", nullable = false, length = 300)
    private String titulo;

    // FAQ, FICHA_TECNICA, MANUAL_SOPORTE, CATALOGO, POLITICA_GARANTIA
    @Column(name = "tipo_documento", length = 50)
    private String tipoDocumento;

    // URL en Azure Blob Storage
    @Column(name = "url_blob", length = 500)
    private String urlBlob;

    // PENDIENTE, PROCESADO, ERROR
    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga = LocalDateTime.now();

    @Column(name = "fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;

    @OneToMany(mappedBy = "documentoConocimiento", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FragmentoConocimiento> fragmentos;

    public DocumentoConocimiento() {}

    public DocumentoConocimiento(Long id, Usuario usuarioCarga, Producto producto,
                                 String titulo, String tipoDocumento, String urlBlob) {
        this.id = id;
        this.usuarioCarga = usuarioCarga;
        this.producto = producto;
        this.titulo = titulo;
        this.tipoDocumento = tipoDocumento;
        this.urlBlob = urlBlob;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuarioCarga() { return usuarioCarga; }
    public void setUsuarioCarga(Usuario usuarioCarga) { this.usuarioCarga = usuarioCarga; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getUrlBlob() { return urlBlob; }
    public void setUrlBlob(String urlBlob) { this.urlBlob = urlBlob; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }

    public LocalDateTime getFechaProcesamiento() { return fechaProcesamiento; }
    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) { this.fechaProcesamiento = fechaProcesamiento; }

    public List<FragmentoConocimiento> getFragmentos() { return fragmentos; }
    public void setFragmentos(List<FragmentoConocimiento> fragmentos) { this.fragmentos = fragmentos; }
}