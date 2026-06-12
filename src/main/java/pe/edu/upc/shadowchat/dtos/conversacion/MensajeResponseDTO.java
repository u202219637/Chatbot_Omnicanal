package pe.edu.upc.shadowchat.dtos.conversacion;
import pe.edu.upc.shadowchat.dtos.fuenterespuesta.FuenteRespuestaDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public class MensajeResponseDTO {
    private Long id;
    private Long conversacionId;
    private String tipoEmisor;
    private String contenido;
    private String intencionDetectada;
    private BigDecimal confianzaIa;
    private Integer tokensEntrada;
    private Integer tokensSalida;
    private LocalDateTime fechaEnvio;
    private List<FuenteRespuestaDTO> fuentes;
    private Boolean escalada;
    public Long getId()                                { return id; }
    public void setId(Long v)                          { this.id = v; }
    public Long getConversacionId()                    { return conversacionId; }
    public void setConversacionId(Long v)              { this.conversacionId = v; }
    public String getTipoEmisor()                      { return tipoEmisor; }
    public void setTipoEmisor(String v)                { this.tipoEmisor = v; }
    public String getContenido()                       { return contenido; }
    public void setContenido(String v)                 { this.contenido = v; }
    public String getIntencionDetectada()              { return intencionDetectada; }
    public void setIntencionDetectada(String v)        { this.intencionDetectada = v; }
    public BigDecimal getConfianzaIa()                 { return confianzaIa; }
    public void setConfianzaIa(BigDecimal v)           { this.confianzaIa = v; }
    public Integer getTokensEntrada()                  { return tokensEntrada; }
    public void setTokensEntrada(Integer v)            { this.tokensEntrada = v; }
    public Integer getTokensSalida()                   { return tokensSalida; }
    public void setTokensSalida(Integer v)             { this.tokensSalida = v; }
    public LocalDateTime getFechaEnvio()               { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime v)         { this.fechaEnvio = v; }
    public List<FuenteRespuestaDTO> getFuentes()       { return fuentes; }
    public void setFuentes(List<FuenteRespuestaDTO> v) { this.fuentes = v; }
    public Boolean getEscalada()                       { return escalada; }
    public void setEscalada(Boolean v)                 { this.escalada = v; }

    private String canalNombre; // WEB o WHATSAPP — para vista omnicanal

    public String getCanalNombre()         { return canalNombre; }
    public void setCanalNombre(String v)   { this.canalNombre = v; }
}