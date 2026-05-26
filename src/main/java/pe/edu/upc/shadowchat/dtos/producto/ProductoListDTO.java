package pe.edu.upc.shadowchat.dtos.producto;
import java.math.BigDecimal;
public class ProductoListDTO {
    private Long id;
    private String nombre;
    private String categoriaNombre;
    private String marcaNombre;
    private String especResumen;
    private BigDecimal precio;
    private Integer stock;
    private String urlImagen;
    private Boolean estado;
    public Long getId()                      { return id; }
    public void setId(Long v)                { this.id = v; }
    public String getNombre()                { return nombre; }
    public void setNombre(String v)          { this.nombre = v; }
    public String getCategoriaNombre()       { return categoriaNombre; }
    public void setCategoriaNombre(String v) { this.categoriaNombre = v; }
    public String getMarcaNombre()           { return marcaNombre; }
    public void setMarcaNombre(String v)     { this.marcaNombre = v; }
    public String getEspecResumen()          { return especResumen; }
    public void setEspecResumen(String v)    { this.especResumen = v; }
    public BigDecimal getPrecio()            { return precio; }
    public void setPrecio(BigDecimal v)      { this.precio = v; }
    public Integer getStock()                { return stock; }
    public void setStock(Integer v)          { this.stock = v; }
    public String getUrlImagen()             { return urlImagen; }
    public void setUrlImagen(String v)       { this.urlImagen = v; }
    public Boolean getEstado()               { return estado; }
    public void setEstado(Boolean v)         { this.estado = v; }
}