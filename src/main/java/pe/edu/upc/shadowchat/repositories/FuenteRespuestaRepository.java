package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.FuenteRespuesta;

import java.util.List;

@Repository
public interface FuenteRespuestaRepository extends JpaRepository<FuenteRespuesta, Long> {

    // Fuentes usadas por un mensaje específico (HU16 — trazabilidad RAG)
    List<FuenteRespuesta> findByMensajeIdOrderByScoreRelevanciaDesc(Long mensajeId);
}