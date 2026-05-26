package pe.edu.upc.shadowchat.dtos.fuenterespuesta;
import java.math.BigDecimal;
public class FuenteRespuestaDTO {
    private Long id;
    private String tipoFuente;
    private String tituloDocumento;
    private String extractoContenido;
    private BigDecimal scoreRelevancia;
    public Long getId()                          { return id; }
    public void setId(Long v)                    { this.id = v; }
    public String getTipoFuente()                { return tipoFuente; }
    public void setTipoFuente(String v)          { this.tipoFuente = v; }
    public String getTituloDocumento()           { return tituloDocumento; }
    public void setTituloDocumento(String v)     { this.tituloDocumento = v; }
    public String getExtractoContenido()         { return extractoContenido; }
    public void setExtractoContenido(String v)   { this.extractoContenido = v; }
    public BigDecimal getScoreRelevancia()       { return scoreRelevancia; }
    public void setScoreRelevancia(BigDecimal v) { this.scoreRelevancia = v; }
}