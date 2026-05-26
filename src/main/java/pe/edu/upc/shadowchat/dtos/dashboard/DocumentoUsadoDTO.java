package pe.edu.upc.shadowchat.dtos.dashboard;
public class DocumentoUsadoDTO {
    private String tituloDocumento;
    private Long usos;
    public String getTituloDocumento()       { return tituloDocumento; }
    public void setTituloDocumento(String v) { this.tituloDocumento = v; }
    public Long getUsos()                    { return usos; }
    public void setUsos(Long v)              { this.usos = v; }
}