package pe.edu.upc.shadowchat.dtos.producto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class ProductoDetalleDTO {
    private Long id;
    private String codigoProducto;
    private String nombre;
    private String categoriaNombre;
    private String marcaNombre;
    private String descripcion;
    private String especificaciones;
    private BigDecimal precio;
    private Integer stock;
    private String urlImagen;
    private Boolean estado;
    private LocalDateTime fechaActualizacion;
    private String recomendacionIa;
    public Long getId()                              { return id; }
    public void setId(Long v)                        { this.id = v; }
    public String getCodigoProducto()                { return codigoProducto; }
    public void setCodigoProducto(String v)          { this.codigoProducto = v; }
    public String getNombre()                        { return nombre; }
    public void setNombre(String v)                  { this.nombre = v; }
    public String getCategoriaNombre()               { return categoriaNombre; }
    public void setCategoriaNombre(String v)         { this.categoriaNombre = v; }
    public String getMarcaNombre()                   { return marcaNombre; }
    public void setMarcaNombre(String v)             { this.marcaNombre = v; }
    public String getDescripcion()                   { return descripcion; }
    public void setDescripcion(String v)             { this.descripcion = v; }
    public String getEspecificaciones()              { return especificaciones; }
    public void setEspecificaciones(String v)        { this.especificaciones = v; }
    public BigDecimal getPrecio()                    { return precio; }
    public void setPrecio(BigDecimal v)              { this.precio = v; }
    public Integer getStock()                        { return stock; }
    public void setStock(Integer v)                  { this.stock = v; }
    public String getUrlImagen()                     { return urlImagen; }
    public void setUrlImagen(String v)               { this.urlImagen = v; }
    public Boolean getEstado()                       { return estado; }
    public void setEstado(Boolean v)                 { this.estado = v; }
    public LocalDateTime getFechaActualizacion()     { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime v){ this.fechaActualizacion = v; }
    public String getRecomendacionIa()               { return recomendacionIa; }
    public void setRecomendacionIa(String v)         { this.recomendacionIa = v; }
}