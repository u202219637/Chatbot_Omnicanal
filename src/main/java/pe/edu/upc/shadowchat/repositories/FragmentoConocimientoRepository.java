package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.shadowchat.entities.FragmentoConocimiento;

import java.util.List;

@Repository
public interface FragmentoConocimientoRepository
        extends JpaRepository<FragmentoConocimiento, Long> {

    /*
     * BÚSQUEDA SEMÁNTICA — núcleo del RAG (HU13, HU16, HU17)
     *
     * Operador <=> de pgvector = distancia coseno.
     * Requiere: CREATE EXTENSION IF NOT EXISTS vector;
     *
     * El CAST es necesario porque Spring pasa float[] como bytea por defecto.
     * Con la dependencia com.pgvector:pgvector:0.1.6 en el pom se puede
     * omitir el CAST y usar directamente el tipo nativo, pero el CAST
     * explícito es más seguro y funciona en ambos casos.
     *
     * topK = 5 es suficiente para el prompt de GPT-4o-mini.
     * Si el contexto supera ~3000 tokens, reducir a 3.
     */
    @Query(value = """
            SELECT *
            FROM fragmento_conocimiento
            WHERE estado = true
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :topK
            """, nativeQuery = true)
    List<FragmentoConocimiento> findTopKBySimilarity(
            @Param("embedding") float[] embedding,
            @Param("topK")      int topK);

    /*
     * Versión con score de similitud para guardar en fuente_respuesta.
     * Retorna: [id, contenido, score]  (Object[])
     */
    @Query(value = """
            SELECT id,
                   contenido,
                   1 - (embedding <=> CAST(:embedding AS vector)) AS score
            FROM fragmento_conocimiento
            WHERE estado = true
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :topK
            """, nativeQuery = true)
    List<Object[]> findTopKWithScore(
            @Param("embedding") float[] embedding,
            @Param("topK")      int topK);

    // Lista fragmentos de un documento para el panel de preview (HU30)
    List<FragmentoConocimiento> findByDocumentoConocimientoIdOrderByOrdenFragmentoAsc(
            Long documentoId);

    // Cuenta fragmentos por documento (HU30 — paginación)
    long countByDocumentoConocimientoId(Long documentoId);

    // Elimina todos los fragmentos de un documento al borrarlo (HU29)
    @Modifying
    @Transactional
    @Query("DELETE FROM FragmentoConocimiento f WHERE f.documentoConocimiento.id = :docId")
    void deleteByDocumentoConocimientoId(@Param("docId") Long docId);

    @Modifying
    @Transactional
    @Query("DELETE FROM FragmentoConocimiento f WHERE f.contenido LIKE %:texto%")
    void deleteByContenidoContaining(@Param("texto") String texto);
    // Busca fragmento por producto exacto para actualizar

    @Query("SELECT f FROM FragmentoConocimiento f WHERE f.contenido LIKE :patron AND f.documentoConocimiento.id = :docId")
    List<FragmentoConocimiento> findByProductoEnDocumento(
            @Param("patron") String patron,
            @Param("docId") Long docId);
}