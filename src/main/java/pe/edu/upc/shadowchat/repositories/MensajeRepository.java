package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.Mensaje;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // Todos los mensajes de una conversación ordenados cronológicamente (HU13, HU18)
    // FIX rendimiento: JOIN FETCH trae canal y usuarioCanal en la misma query.
    // Este endpoint se llama cada 5s por el polling del chat (cliente y panel
    // de asesor) — sin esto, cada poll generaba N+1 queries adicionales que,
    // bajo el plan B1 (CPU compartida), hacían que la respuesta tardara tanto
    // que el siguiente intervalo de polling ya se había disparado antes de
    // que la UI procesara la respuesta anterior — se sentía "congelado" hasta
    // que un click forzaba a Angular a procesar el backlog de respuestas.
    @Query("""
            SELECT m FROM Mensaje m
            LEFT JOIN FETCH m.canal
            LEFT JOIN FETCH m.usuarioCanal
            WHERE m.conversacion.id = :convId
            ORDER BY m.fechaEnvio ASC
            """)
    List<Mensaje> findByConversacionIdOrderByFechaEnvioAsc(@Param("convId") Long convId);

    // Último mensaje del bot — para contexto de respuesta (HU14)
    @Query("""
            SELECT m FROM Mensaje m
            WHERE m.conversacion.id = :convId
              AND m.tipoEmisor = 'BOT'
            ORDER BY m.fechaEnvio DESC
            LIMIT 1
            """)
    java.util.Optional<Mensaje> findUltimoMensajeBot(@Param("convId") Long convId);

    // Total de tokens consumidos por conversación (HU31)
    @Query(value = """
            SELECT SUM(tokens_entrada), SUM(tokens_salida)
            FROM mensaje
            WHERE id_conversacion = :convId
              AND tipo_emisor = 'BOT'
            """, nativeQuery = true)
    Object[] sumaTokensByConversacion(@Param("convId") Long convId);

    // Documentos más usados en respuestas RAG (HU26)
    @Query(value = """
            SELECT dc.titulo, COUNT(fr.id) AS usos
            FROM fuente_respuesta fr
            JOIN fragmento_conocimiento fk ON fr.id_fragmento = fk.id
            JOIN documento_conocimiento dc ON fk.id_documento = dc.id
            GROUP BY dc.titulo
            ORDER BY usos DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> documentosMasUsados();
    List<Mensaje> findTop6ByConversacionIdOrderByFechaEnvioAsc(Long conversacionId);
    List<Mensaje> findTop12ByConversacionIdOrderByFechaEnvioAsc(Long conversacionId);
    long countByConversacionIdAndTipoEmisor(Long conversacionId, String tipoEmisor);
}