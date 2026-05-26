package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Producto;

import java.math.BigDecimal;
import java.util.List;

public interface IProductoService {

    // CRUD base (HU32 — admin)
    List<Producto> list();
    void insert(Producto producto);
    Producto searchId(Long id);
    void update(Producto producto);
    void delete(Long id);
    void cambiarEstado(Long id);

    // Catálogo público activo (HU07)
    List<Producto> listActivos();

    // Filtros para catálogo (HU08)
    List<Producto> filtrar(Long categoriaId, Long marcaId,
                           BigDecimal precioMin, BigDecimal precioMax);

    // Búsqueda por texto (HU09)
    List<Producto> buscar(String q);

    // Comparación de productos (HU12)
    List<Producto> comparar(List<Long> ids);
}