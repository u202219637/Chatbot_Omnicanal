package pe.edu.upc.shadowchat.dtos.documento;
public class DocumentoUploadDTO {
    private Long productoId;
    private String tipoDocumento;
    public Long getProductoId()              { return productoId; }
    public void setProductoId(Long v)        { this.productoId = v; }
    public String getTipoDocumento()         { return tipoDocumento; }
    public void setTipoDocumento(String v)   { this.tipoDocumento = v; }
}