package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.UsuarioCanal;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioCanalRepository extends JpaRepository<UsuarioCanal, Long> {

    /*
     * CLAVE para omnicanalidad (HU06, HU20):
     * Cuando llega un webhook de Twilio con el número +51999999999,
     * este método resuelve qué usuario del sistema es.
     * identificador_externo tiene unique constraint (canal + identificador).
     */
    Optional<UsuarioCanal> findByCanalNombreAndIdentificadorExterno(
            String canalNombre, String identificadorExterno);

    // Lista todos los canales vinculados de un usuario (perfil HU05)
    List<UsuarioCanal> findByUsuarioIdAndActivoTrue(Long usuarioId);

    // Verifica si ya existe el vínculo antes de crear uno duplicado
    boolean existsByCanalIdAndIdentificadorExterno(Long canalId,
                                                   String identificadorExterno);
}