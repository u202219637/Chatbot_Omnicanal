package pe.edu.upc.shadowchat.dtos.dashboard;

public class PalabraFrecuenteDTO {
    private String palabra;
    private Long total;

    public String getPalabra() { return palabra; }
    public void setPalabra(String v) { this.palabra = v; }
    public Long getTotal() { return total; }
    public void setTotal(Long v) { this.total = v; }
}