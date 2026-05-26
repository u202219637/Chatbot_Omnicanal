package pe.edu.upc.shadowchat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_canal", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_canal", "identificador_externo"})
})
public class UsuarioCanal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_canal", nullable = false)
    private Canal canal;

    // Numero WhatsApp (+51999999999) o correo para Web
    @Column(name = "identificador_externo", nullable = false, length = 300)
    private String identificadorExterno;

    // Nombre del contacto en el canal (nombre en WhatsApp, etc.)
    @Column(name = "nombre_externo", length = 200)
    private String nombreExterno;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_vinculacion")
    private LocalDateTime fechaVinculacion = LocalDateTime.now();

    public UsuarioCanal() {}

    public UsuarioCanal(Long id, Usuario usuario, Canal canal,
                        String identificadorExterno, String nombreExterno, Boolean activo) {
        this.id = id;
        this.usuario = usuario;
        this.canal = canal;
        this.identificadorExterno = identificadorExterno;
        this.nombreExterno = nombreExterno;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Canal getCanal() { return canal; }
    public void setCanal(Canal canal) { this.canal = canal; }

    public String getIdentificadorExterno() { return identificadorExterno; }
    public void setIdentificadorExterno(String identificadorExterno) { this.identificadorExterno = identificadorExterno; }

    public String getNombreExterno() { return nombreExterno; }
    public void setNombreExterno(String nombreExterno) { this.nombreExterno = nombreExterno; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaVinculacion() { return fechaVinculacion; }
    public void setFechaVinculacion(LocalDateTime fechaVinculacion) { this.fechaVinculacion = fechaVinculacion; }
}