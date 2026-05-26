package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.shadowchat.entities.Rol;

import java.util.List;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    // Igual que AutoGuard — lista roles de un usuario
    List<Rol> findByUsuarioId(Long usuarioId);

    // Evita duplicar roles al asignar (HU24)
    boolean existsByUsuarioIdAndRol(Long usuarioId, String rol);

    // Elimina un rol específico sin borrar el usuario (HU24)
    @Modifying
    @Transactional
    @Query("DELETE FROM Rol r WHERE r.usuario.id = :usuarioId AND r.rol = :rol")
    void deleteByUsuarioIdAndRol(@Param("usuarioId") Long usuarioId,
                                 @Param("rol") String rol);
}