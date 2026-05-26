package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.Producto;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Catálogo activo (HU07)
    List<Producto> findByEstadoTrue();

    // Filtros por categoría y marca (HU08)
    List<Producto> findByCategoriaIdAndEstadoTrue(Long categoriaId);
    List<Producto> findByMarcaIdAndEstadoTrue(Long marcaId);
    List<Producto> findByCategoriaIdAndMarcaIdAndEstadoTrue(Long categoriaId, Long marcaId);

    // Búsqueda por nombre o especificaciones (HU09)
    @Query("""
            SELECT p FROM Producto p
            WHERE p.estado = true
              AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.especificaciones) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY p.nombre
            """)
    List<Producto> searchByTexto(@Param("q") String q);

    // Filtro por rango de precio (HU08)
    @Query("""
            SELECT p FROM Producto p
            WHERE p.estado = true
              AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
              AND (:marcaId IS NULL OR p.marca.id = :marcaId)
              AND (:precioMin IS NULL OR p.precio >= :precioMin)
              AND (:precioMax IS NULL OR p.precio <= :precioMax)
            ORDER BY p.precio
            """)
    List<Producto> filtrar(@Param("categoriaId") Long categoriaId,
                           @Param("marcaId")     Long marcaId,
                           @Param("precioMin")   BigDecimal precioMin,
                           @Param("precioMax")   BigDecimal precioMax);

    // Para comparación de productos (HU12)
    List<Producto> findByIdIn(List<Long> ids);
}