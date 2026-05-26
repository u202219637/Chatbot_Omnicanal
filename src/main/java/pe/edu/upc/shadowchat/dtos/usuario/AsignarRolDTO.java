package pe.edu.upc.shadowchat.dtos.usuario;
public class AsignarRolDTO {
    private String rol;    // CLIENTE | ASESOR | ADMINISTRADOR
    private String accion; // ASIGNAR | QUITAR
    public String getRol()              { return rol; }
    public void setRol(String v)        { this.rol = v; }
    public String getAccion()           { return accion; }
    public void setAccion(String v)     { this.accion = v; }
}