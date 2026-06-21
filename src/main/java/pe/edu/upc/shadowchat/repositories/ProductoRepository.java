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
    // FIX rendimiento: JOIN FETCH trae categoria y marca en la MISMA query SQL,
    // evitando el patrón N+1 (antes: 1 query de productos + 1 query por cada
    // producto para su categoria + 1 query por cada producto para su marca —
    // con 29 productos eso eran ~59 queries en vez de 1).
    @Query("""
            SELECT p FROM Producto p
            JOIN FETCH p.categoria
            JOIN FETCH p.marca
            WHERE p.estado = true
            """)
    List<Producto> findByEstadoTrue();

    // Filtros por categoría y marca (HU08)
    @Query("""
            SELECT p FROM Producto p
            JOIN FETCH p.categoria
            JOIN FETCH p.marca
            WHERE p.categoria.id = :categoriaId AND p.estado = true
            """)
    List<Producto> findByCategoriaIdAndEstadoTrue(@Param("categoriaId") Long categoriaId);

    @Query("""
            SELECT p FROM Producto p
            JOIN FETCH p.categoria
            JOIN FETCH p.marca
            WHERE p.marca.id = :marcaId AND p.estado = true
            """)
    List<Producto> findByMarcaIdAndEstadoTrue(@Param("marcaId") Long marcaId);

    @Query("""
            SELECT p FROM Producto p
            JOIN FETCH p.categoria
            JOIN FETCH p.marca
            WHERE p.categoria.id = :categoriaId AND p.marca.id = :marcaId AND p.estado = true
            """)
    List<Producto> findByCategoriaIdAndMarcaIdAndEstadoTrue(@Param("categoriaId") Long categoriaId,
                                                            @Param("marcaId") Long marcaId);

    // Búsqueda por nombre o especificaciones (HU09)
    @Query("""
            SELECT p FROM Producto p
            JOIN FETCH p.categoria
            JOIN FETCH p.marca
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
            JOIN FETCH p.categoria
            JOIN FETCH p.marca
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
    @Query("""
            SELECT p FROM Producto p
            JOIN FETCH p.categoria
            JOIN FETCH p.marca
            WHERE p.id IN :ids
            """)
    List<Producto> findByIdIn(@Param("ids") List<Long> ids);
}