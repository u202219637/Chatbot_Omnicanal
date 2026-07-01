package pe.edu.upc.shadowchat.dtos.usuario;

import java.util.Map;

public class UsuarioStatsDTO {
    private long total;
    private long activos;
    private long inactivos;
    private Map<String, Long> porRol;

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getActivos() { return activos; }
    public void setActivos(long activos) { this.activos = activos; }
    public long getInactivos() { return inactivos; }
    public void setInactivos(long inactivos) { this.inactivos = inactivos; }
    public Map<String, Long> getPorRol() { return porRol; }
    public void setPorRol(Map<String, Long> porRol) { this.porRol = porRol; }
}