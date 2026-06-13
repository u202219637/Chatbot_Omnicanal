package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Rol;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.repositories.RolRepository;
import pe.edu.upc.shadowchat.repositories.UsuarioRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImplement implements IUsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired
    @org.springframework.context.annotation.Lazy
    private pe.edu.upc.shadowchat.repositories.CanalRepository canalRepository;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private pe.edu.upc.shadowchat.repositories.UsuarioCanalRepository usuarioCanalRepository;
    @Override
    public List<Usuario> list() {
        return usuarioRepository.findAll();
    }

    @Override
    public void insert(Usuario usuario) {
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        usuarioRepository.save(usuario);

        // Rol CLIENTE por defecto
        if (rolRepository.findByUsuarioId(usuario.getId()).isEmpty()) {
            Rol rol = new Rol();
            rol.setRol("CLIENTE");
            rol.setUsuario(usuario);
            rolRepository.save(rol);
        }

        // Auto-vincular canal WEB al registrarse
        try {
            pe.edu.upc.shadowchat.entities.Canal canalWeb =
                    canalRepository.findByNombre("WEB").orElse(null);
            if (canalWeb != null) {
                pe.edu.upc.shadowchat.entities.UsuarioCanal uc =
                        new pe.edu.upc.shadowchat.entities.UsuarioCanal();
                uc.setUsuario(usuario);
                uc.setCanal(canalWeb);
                String telefono = usuario.getTelefono();
                if (telefono != null && !telefono.isEmpty()) {
                    uc.setIdentificadorExterno(telefono);
                } else {
                    uc.setIdentificadorExterno(usuario.getUsername());
                }
                uc.setNombreExterno(usuario.getNombres() + " " + usuario.getApellidos());
                uc.setActivo(true);
                usuarioCanalRepository.save(uc);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public Usuario searchId(Long id) {
        return usuarioRepository.findById(id).orElse(new Usuario());
    }

    @Override
    public void update(Usuario usuario) {
        // Mantiene la FK de roles apuntando al usuario (mismo fix que AutoGuard)
        if (usuario.getRoles() != null) {
            for (Rol rol : usuario.getRoles()) {
                rol.setUsuario(usuario);
            }
        }
        usuarioRepository.save(usuario);
    }

    @Override
    public void delete(Long id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        opt.ifPresent(u -> {
            rolRepository.findByUsuarioId(id).forEach(rolRepository::delete);
            usuarioRepository.deleteById(id);
        });
    }

    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Correo no encontrado: " + correo));
    }

    @Override
    public void cambiarEstado(Long id) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setEstado(!Boolean.TRUE.equals(u.getEstado()));
            usuarioRepository.save(u);
        });
    }

    @Override
    public List<Object[]> countByRol() {
        return usuarioRepository.countByRol();
    }
}