package pe.edu.upc.shadowchat.dtos.usuario;
import java.time.LocalDateTime;
import java.util.List;
public class UsuarioAdminDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String username;
    private Boolean estado;
    private LocalDateTime fechaRegistro;
    private List<String> roles;
    public Long getId()                          { return id; }
    public void setId(Long v)                    { this.id = v; }
    public String getNombres()                   { return nombres; }
    public void setNombres(String v)             { this.nombres = v; }
    public String getApellidos()                 { return apellidos; }
    public void setApellidos(String v)           { this.apellidos = v; }
    public String getCorreo()                    { return correo; }
    public void setCorreo(String v)              { this.correo = v; }
    public String getTelefono()                  { return telefono; }
    public void setTelefono(String v)            { this.telefono = v; }
    public String getUsername()                  { return username; }
    public void setUsername(String v)            { this.username = v; }
    public Boolean getEstado()                   { return estado; }
    public void setEstado(Boolean v)             { this.estado = v; }
    public LocalDateTime getFechaRegistro()      { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime v){ this.fechaRegistro = v; }
    public List<String> getRoles()               { return roles; }
    public void setRoles(List<String> v)         { this.roles = v; }
}