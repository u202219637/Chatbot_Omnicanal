package pe.edu.upc.shadowchat.dtos.dashboard;
public class IntencionFrecuenteDTO {
    private String intencion;
    private Long frecuencia;
    private Double porcentaje;
    public String getIntencion()             { return intencion; }
    public void setIntencion(String v)       { this.intencion = v; }
    public Long getFrecuencia()              { return frecuencia; }
    public void setFrecuencia(Long v)        { this.frecuencia = v; }
    public Double getPorcentaje()            { return porcentaje; }
    public void setPorcentaje(Double v)      { this.porcentaje = v; }
}