package pe.edu.upc.shadowchat.dtos.producto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
public class ProductoFormDTO {
    private Long id;
    @NotNull  private Long categoriaId;
    @NotNull  private Long marcaId;
    @NotBlank private String codigoProducto;
    @NotBlank private String nombre;
    private String descripcion;
    private String especificaciones;
    @NotNull  private BigDecimal precio;
    private Integer stock;
    private String urlImagen;
    private Boolean estado;
    public Long getId()                      { return id; }
    public void setId(Long v)                { this.id = v; }
    public Long getCategoriaId()             { return categoriaId; }
    public void setCategoriaId(Long v)       { this.categoriaId = v; }
    public Long getMarcaId()                 { return marcaId; }
    public void setMarcaId(Long v)           { this.marcaId = v; }
    public String getCodigoProducto()        { return codigoProducto; }
    public void setCodigoProducto(String v)  { this.codigoProducto = v; }
    public String getNombre()                { return nombre; }
    public void setNombre(String v)          { this.nombre = v; }
    public String getDescripcion()           { return descripcion; }
    public void setDescripcion(String v)     { this.descripcion = v; }
    public String getEspecificaciones()      { return especificaciones; }
    public void setEspecificaciones(String v){ this.especificaciones = v; }
    public BigDecimal getPrecio()            { return precio; }
    public void setPrecio(BigDecimal v)      { this.precio = v; }
    public Integer getStock()                { return stock; }
    public void setStock(Integer v)          { this.stock = v; }
    public String getUrlImagen()             { return urlImagen; }
    public void setUrlImagen(String v)       { this.urlImagen = v; }
    public Boolean getEstado()               { return estado; }
    public void setEstado(Boolean v)         { this.estado = v; }
}