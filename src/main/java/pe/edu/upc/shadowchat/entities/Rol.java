package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "rol"})
})
public class Rol implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol; // CLIENTE, ADMINISTRADOR, ASESOR

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    public Rol() {}

    public Rol(Long id, String rol, Usuario usuario) {
        this.id = id;
        this.rol = rol;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}