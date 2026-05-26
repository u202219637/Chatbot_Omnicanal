package pe.edu.upc.shadowchat.dtos.documento;
public class RagEstadisticasDTO {
    private Long documentosCargados;
    private Long bloquesInformacion;
    private Long tokensTotalesProcesados;
    private Double porcentajeCompletado;
    public Long getDocumentosCargados()                  { return documentosCargados; }
    public void setDocumentosCargados(Long v)            { this.documentosCargados = v; }
    public Long getBloquesInformacion()                  { return bloquesInformacion; }
    public void setBloquesInformacion(Long v)            { this.bloquesInformacion = v; }
    public Long getTokensTotalesProcesados()             { return tokensTotalesProcesados; }
    public void setTokensTotalesProcesados(Long v)       { this.tokensTotalesProcesados = v; }
    public Double getPorcentajeCompletado()              { return porcentajeCompletado; }
    public void setPorcentajeCompletado(Double v)        { this.porcentajeCompletado = v; }
}