package pe.edu.upc.shadowchat.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "canal")
public class Canal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // WEB, WHATSAPP
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "activo")
    private Boolean activo = true;

    @OneToMany(mappedBy = "canal", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<UsuarioCanal> usuariosCanal;

    @OneToMany(mappedBy = "canal", fetch = FetchType.LAZY)
    private List<Mensaje> mensajes;

    public Canal() {}

    public Canal(Long id, String nombre, String descripcion, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public List<UsuarioCanal> getUsuariosCanal() { return usuariosCanal; }
    public void setUsuariosCanal(List<UsuarioCanal> usuariosCanal) { this.usuariosCanal = usuariosCanal; }

    public List<Mensaje> getMensajes() { return mensajes; }
    public void setMensajes(List<Mensaje> mensajes) { this.mensajes = mensajes; }
}