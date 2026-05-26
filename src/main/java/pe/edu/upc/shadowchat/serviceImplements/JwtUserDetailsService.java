package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // findOneByUsername devuelve null si no existe — mismo patrón AutoGuard
        Usuario user = usuarioRepository.findOneByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        user.getRoles().forEach(rol ->
                roles.add(new SimpleGrantedAuthority(rol.getRol()))
        );

        // CLAVE: usa getPasswordHash() porque así se llama el campo en la entidad Usuario
        // AutoGuard usaba getPassword() — en Shadowchat el getter es getPasswordHash()
        // pero la entidad ya tiene getPassword() como alias que retorna passwordHash
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),     // getPassword() en Usuario.java retorna passwordHash
                user.isEnabled(),
                true, true, true,
                roles
        );
    }
}