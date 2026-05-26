package pe.edu.upc.shadowchat.dtos.documento;
import java.time.LocalDateTime;
public class DocumentoListDTO {
    private Long id;
    private String titulo;
    private String tipoDocumento;
    private String estado;
    private LocalDateTime fechaCarga;
    private LocalDateTime fechaProcesamiento;
    private Long cantidadFragmentos;
    private String urlBlob;
    private String productoNombre;
    public Long getId()                                  { return id; }
    public void setId(Long v)                            { this.id = v; }
    public String getTitulo()                            { return titulo; }
    public void setTitulo(String v)                      { this.titulo = v; }
    public String getTipoDocumento()                     { return tipoDocumento; }
    public void setTipoDocumento(String v)               { this.tipoDocumento = v; }
    public String getEstado()                            { return estado; }
    public void setEstado(String v)                      { this.estado = v; }
    public LocalDateTime getFechaCarga()                 { return fechaCarga; }
    public void setFechaCarga(LocalDateTime v)           { this.fechaCarga = v; }
    public LocalDateTime getFechaProcesamiento()         { return fechaProcesamiento; }
    public void setFechaProcesamiento(LocalDateTime v)   { this.fechaProcesamiento = v; }
    public Long getCantidadFragmentos()                  { return cantidadFragmentos; }
    public void setCantidadFragmentos(Long v)            { this.cantidadFragmentos = v; }
    public String getUrlBlob()                           { return urlBlob; }
    public void setUrlBlob(String v)                     { this.urlBlob = v; }
    public String getProductoNombre()                    { return productoNombre; }
    public void setProductoNombre(String v)              { this.productoNombre = v; }
}