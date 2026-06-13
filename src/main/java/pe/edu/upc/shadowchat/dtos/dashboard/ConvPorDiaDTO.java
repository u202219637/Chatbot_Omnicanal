package pe.edu.upc.shadowchat.dtos.dashboard;

public class ConvPorDiaDTO {
    private String dia;
    private long totalWeb;
    private long totalWhatsapp;
    private long total;
    // getters y setters
    public String getDia()              { return dia; }
    public void setDia(String v)        { this.dia = v; }
    public long getTotalWeb()           { return totalWeb; }
    public void setTotalWeb(long v)     { this.totalWeb = v; }
    public long getTotalWhatsapp()      { return totalWhatsapp; }
    public void setTotalWhatsapp(long v){ this.totalWhatsapp = v; }
    public long getTotal()              { return total; }
    public void setTotal(long v)        { this.total = v; }
}