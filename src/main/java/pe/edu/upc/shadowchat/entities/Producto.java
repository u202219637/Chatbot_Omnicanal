package pe.edu.upc.shadowchat.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "producto")
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_marca", nullable = false)
    private Marca marca;

    @Column(name = "codigo_producto", unique = true, length = 50)
    private String codigoProducto;

    @Column(name = "nombre", nullable = false, length = 300)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "especificaciones", columnDefinition = "TEXT")
    private String especificaciones;

    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock")
    private Integer stock = 0;

    // URL de imagen simplificada - sin tabla ProductoImagen
    @Column(name = "url_imagen", length = 500)
    private String urlImagen;

    @Column(name = "estado")
    private Boolean estado = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relacion con documentos RAG (nullable: no todos los productos tienen doc)
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DocumentoConocimiento> documentos;

    public Producto() {}

    public Producto(Long id, Categoria categoria, Marca marca, String codigoProducto,
                    String nombre, String descripcion, String especificaciones,
                    BigDecimal precio, Integer stock, String urlImagen, Boolean estado) {
        this.id = id;
        this.categoria = categoria;
        this.marca = marca;
        this.codigoProducto = codigoProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.especificaciones = especificaciones;
        this.precio = precio;
        this.stock = stock;
        this.urlImagen = urlImagen;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEspecificaciones() { return especificaciones; }
    public void setEspecificaciones(String especificaciones) { this.especificaciones = especificaciones; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public List<DocumentoConocimiento> getDocumentos() { return documentos; }
    public void setDocumentos(List<DocumentoConocimiento> documentos) { this.documentos = documentos; }
}