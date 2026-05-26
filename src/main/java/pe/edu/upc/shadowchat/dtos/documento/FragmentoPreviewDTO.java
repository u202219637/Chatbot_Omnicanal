package pe.edu.upc.shadowchat.dtos.documento;
public class FragmentoPreviewDTO {
    private Long id;
    private Integer ordenFragmento;
    private Integer cantidadTokens;
    private String contenidoExtracto;
    private Boolean estado;
    public Long getId()                          { return id; }
    public void setId(Long v)                    { this.id = v; }
    public Integer getOrdenFragmento()           { return ordenFragmento; }
    public void setOrdenFragmento(Integer v)     { this.ordenFragmento = v; }
    public Integer getCantidadTokens()           { return cantidadTokens; }
    public void setCantidadTokens(Integer v)     { this.cantidadTokens = v; }
    public String getContenidoExtracto()         { return contenidoExtracto; }
    public void setContenidoExtracto(String v)   { this.contenidoExtracto = v; }
    public Boolean getEstado()                   { return estado; }
    public void setEstado(Boolean v)             { this.estado = v; }
}