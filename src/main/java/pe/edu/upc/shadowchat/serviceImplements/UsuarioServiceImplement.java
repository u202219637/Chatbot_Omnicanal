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

    @Override
    public List<Usuario> list() {
        return usuarioRepository.findAll();
    }

    @Override
    public void insert(Usuario usuario) {
        // BCrypt — igual que AutoGuard, nunca texto plano
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        usuarioRepository.save(usuario);

        // Rol CLIENTE por defecto si no viene con roles
        if (rolRepository.findByUsuarioId(usuario.getId()).isEmpty()) {
            Rol rol = new Rol();
            rol.setRol("CLIENTE");
            rol.setUsuario(usuario);
            rolRepository.save(rol);
        }
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