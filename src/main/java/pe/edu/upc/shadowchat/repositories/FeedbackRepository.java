package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.Feedback;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Feedbacks de una conversación (HU23)
    List<Feedback> findByConversacionId(Long conversacionId);

    // Distribución de calificaciones 1-5 (HU25 — dashboard)
    @Query(value = """
            SELECT calificacion, COUNT(*) AS total
            FROM feedback
            GROUP BY calificacion
            ORDER BY calificacion
            """, nativeQuery = true)
    List<Object[]> distribucionCalificaciones();

    // Motivos más frecuentes (HU26)
    @Query(value = """
            SELECT motivo, COUNT(*) AS total
            FROM feedback
            WHERE motivo IS NOT NULL
            GROUP BY motivo
            ORDER BY total DESC
            """, nativeQuery = true)
    List<Object[]> topMotivos();
}