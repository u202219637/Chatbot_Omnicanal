package pe.edu.upc.shadowchat.dtos.usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class UsuarioRegistroDTO {
    @NotBlank private String nombres;
    @NotBlank private String apellidos;
    @Email @NotBlank private String correo;
    private String telefono;
    @NotBlank @Size(min=8) private String username;
    @NotBlank @Size(min=8) private String password;
    public String getNombres()           { return nombres; }
    public void setNombres(String v)     { this.nombres = v; }
    public String getApellidos()         { return apellidos; }
    public void setApellidos(String v)   { this.apellidos = v; }
    public String getCorreo()            { return correo; }
    public void setCorreo(String v)      { this.correo = v; }
    public String getTelefono()          { return telefono; }
    public void setTelefono(String v)    { this.telefono = v; }
    public String getUsername()          { return username; }
    public void setUsername(String v)    { this.username = v; }
    public String getPassword()          { return password; }
    public void setPassword(String v)    { this.password = v; }
}