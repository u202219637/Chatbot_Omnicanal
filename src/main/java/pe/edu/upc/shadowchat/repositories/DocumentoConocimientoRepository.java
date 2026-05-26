package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.DocumentoConocimiento;

import java.util.List;

@Repository
public interface DocumentoConocimientoRepository
        extends JpaRepository<DocumentoConocimiento, Long> {

    // Panel admin — lista todos, ordenados por fecha (HU15, HU29)
    List<DocumentoConocimiento> findAllByOrderByFechaCargaDesc();

    // Filtrar por estado: PENDIENTE, PROCESADO, ERROR (HU33)
    List<DocumentoConocimiento> findByEstadoOrderByFechaCargaDesc(String estado);

    // Documentos de un producto específico (HU10 — detalle con doc RAG)
    List<DocumentoConocimiento> findByProductoIdAndEstado(Long productoId, String estado);

    // Verificar si ya existe un documento con el mismo título (evita duplicados)
    boolean existsByTituloAndEstado(String titulo, String estado);
}