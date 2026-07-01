package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.FuenteRespuesta;

import java.util.List;

@Repository
public interface FuenteRespuestaRepository extends JpaRepository<FuenteRespuesta, Long> {

    // Fuentes usadas por un mensaje específico (HU16 — trazabilidad RAG)
    List<FuenteRespuesta> findByMensajeIdOrderByScoreRelevanciaDesc(Long mensajeId);

    // FIX rendimiento: trae TODAS las fuentes de TODOS los mensajes de una
    // conversación en una sola query, en vez de hacer una query por cada
    // mensaje (esto es lo que generaba el N+1 dentro de GET /chat/{id}/mensajes,
    // multiplicado además por el polling cada 5s del chat).
    @Query("""
        SELECT fr FROM FuenteRespuesta fr
        WHERE fr.mensaje.id IN :mensajeIds
        ORDER BY fr.scoreRelevancia DESC
        """)
    List<FuenteRespuesta> findByMensajeIdIn(@Param("mensajeIds") List<Long> mensajeIds);
}