package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.Canal;

import java.util.Optional;

@Repository
public interface CanalRepository extends JpaRepository<Canal, Long> {

    // Busca WEB o WHATSAPP por nombre exacto
    Optional<Canal> findByNombre(String nombre);
}