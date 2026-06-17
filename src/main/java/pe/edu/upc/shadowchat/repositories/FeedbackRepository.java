package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // ── HU31: Satisfacción por asesor ────────────────────────────────

    // Promedio y total de calificaciones de los casos atendidos por un asesor
    @Query(value = """
            SELECT AVG(f.calificacion) AS promedio, COUNT(*) AS total
            FROM feedback f
            JOIN conversacion c ON c.id = f.id_conversacion
            JOIN escalacion e ON e.id_conversacion = c.id
            WHERE e.id_asesor = :asesorId
            """, nativeQuery = true)
    Object[] resumenPorAsesor(@Param("asesorId") Long asesorId);

    // Distribución 1-5 de calificaciones de un asesor específico
    @Query(value = """
            SELECT f.calificacion, COUNT(*) AS total
            FROM feedback f
            JOIN conversacion c ON c.id = f.id_conversacion
            JOIN escalacion e ON e.id_conversacion = c.id
            WHERE e.id_asesor = :asesorId
            GROUP BY f.calificacion
            ORDER BY f.calificacion
            """, nativeQuery = true)
    List<Object[]> distribucionPorAsesor(@Param("asesorId") Long asesorId);

    // Últimos comentarios de clientes sobre los casos de un asesor
    @Query(value = """
            SELECT f.calificacion, f.motivo, f.comentario, f.fecha_registro,
                   u.nombres, u.apellidos
            FROM feedback f
            JOIN conversacion c ON c.id = f.id_conversacion
            JOIN escalacion e ON e.id_conversacion = c.id
            JOIN usuario u ON u.id = f.id_usuario
            WHERE e.id_asesor = :asesorId
            ORDER BY f.fecha_registro DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> comentariosRecientesPorAsesor(@Param("asesorId") Long asesorId);

    @Query(value = """
        SELECT f.calificacion, f.motivo, f.comentario, f.fecha_registro,
               u.nombres, u.apellidos
        FROM feedback f
        JOIN usuario u ON u.id = f.id_usuario
        WHERE f.comentario IS NOT NULL AND f.comentario != ''
        ORDER BY f.fecha_registro DESC
        LIMIT 15
        """, nativeQuery = true)
    List<Object[]> comentariosRecientesGlobal();

    @Query(value = """
        SELECT palabra, COUNT(*) AS total
        FROM (
            SELECT LOWER(unnest(regexp_split_to_array(
                       regexp_replace(comentario, '[^\\wÁÉÍÓÚáéíóúñÑ ]', '', 'g'),
                       '\\s+'
                   ))) AS palabra
            FROM feedback
            WHERE comentario IS NOT NULL AND comentario != ''
        ) sub
        WHERE LENGTH(palabra) > 3
          AND palabra NOT IN (
              'para','pero','esta','este','como','también','tambien','muy',
              'con','que','los','las','una','uno','del','por','más','mas',
              'fue','era','son','han','fueron','todo','toda','todos','todas',
              'porque','cuando','donde','tiene','tuvo','hacer','hizo','sido',
              'estaba','estuvo','solo','sólo','desde','hasta','sobre','entre'
          )
        GROUP BY palabra
        ORDER BY total DESC
        LIMIT 15
        """, nativeQuery = true)
    List<Object[]> palabrasFrecuentes();
}