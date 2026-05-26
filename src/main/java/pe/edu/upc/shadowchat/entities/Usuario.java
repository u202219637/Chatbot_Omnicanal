package pe.edu.upc.shadowchat.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombres", nullable = false, length = 200)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 200)
    private String apellidos;

    @Column(name = "correo", nullable = false, unique = true, length = 300)
    private String correo;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "username", nullable = false, unique = true, length = 300)
    private String username;

    @Column(name = "estado")
    private Boolean estado = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    // Relacion con roles - igual patron que AutoGuard
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Rol> roles;

    // Relacion con conversaciones
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Conversacion> conversaciones;

    // Relacion con canales vinculados (WhatsApp, Web)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UsuarioCanal> canalesVinculados;

    public Usuario() {}

    public Usuario(Long id, String nombres, String apellidos, String correo,
                   String telefono, String passwordHash, String username,
                   Boolean estado, LocalDateTime fechaRegistro,
                   LocalDateTime ultimoAcceso, List<Rol> roles) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.passwordHash = passwordHash;
        this.username = username;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.ultimoAcceso = ultimoAcceso;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public List<Rol> getRoles() { return roles; }
    public void setRoles(List<Rol> roles) { this.roles = roles; }

    public List<Conversacion> getConversaciones() { return conversaciones; }
    public void setConversaciones(List<Conversacion> conversaciones) { this.conversaciones = conversaciones; }

    public List<UsuarioCanal> getCanalesVinculados() { return canalesVinculados; }
    public void setCanalesVinculados(List<UsuarioCanal> canalesVinculados) { this.canalesVinculados = canalesVinculados; }

    // Necesario para Spring Security - igual que AutoGuard
    public boolean isEnabled() { return estado != null && estado; }
    public String getPassword() { return passwordHash; }
}