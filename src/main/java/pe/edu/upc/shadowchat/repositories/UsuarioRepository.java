package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // findByUsername devuelve Optional — usado por JwtUserDetailsService
    Optional<Usuario> findByUsername(String username);

    // Alias sin Optional — usado por JwtUserDetailsService.loadUserByUsername
    // para mantener compatibilidad con el patrón AutoGuard (findOneByUsername)
    default Usuario findOneByUsername(String username) {
        return findByUsername(username).orElse(null);
    }

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByCorreoOrUsername(String correo, String username);

    @Query(value = """
            SELECT DISTINCT u.*
            FROM usuario u
            JOIN roles r ON r.user_id = u.id
            WHERE r.rol = :rol AND u.estado = true
            ORDER BY u.apellidos
            """, nativeQuery = true)
    List<Usuario> findByRol(@Param("rol") String rol);

    @Query(value = """
            SELECT r.rol, COUNT(DISTINCT u.id) AS total
            FROM usuario u
            JOIN roles r ON r.user_id = u.id
            GROUP BY r.rol
            ORDER BY r.rol
            """, nativeQuery = true)
    List<Object[]> countByRol();
    Optional<Usuario> findByTelefono(String telefono);
}