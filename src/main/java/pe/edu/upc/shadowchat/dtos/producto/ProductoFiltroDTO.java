package pe.edu.upc.shadowchat.dtos.producto;
import java.math.BigDecimal;
public class ProductoFiltroDTO {
    private Long categoriaId;
    private Long marcaId;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private String q;
    public Long getCategoriaId()             { return categoriaId; }
    public void setCategoriaId(Long v)       { this.categoriaId = v; }
    public Long getMarcaId()                 { return marcaId; }
    public void setMarcaId(Long v)           { this.marcaId = v; }
    public BigDecimal getPrecioMin()         { return precioMin; }
    public void setPrecioMin(BigDecimal v)   { this.precioMin = v; }
    public BigDecimal getPrecioMax()         { return precioMax; }
    public void setPrecioMax(BigDecimal v)   { this.precioMax = v; }
    public String getQ()                     { return q; }
    public void setQ(String v)               { this.q = v; }
}