package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Producto;
import pe.edu.upc.shadowchat.repositories.ProductoRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IProductoService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoServiceImplement implements IProductoService {

    @Autowired private ProductoRepository productoRepository;

    @Override public List<Producto> list()            { return productoRepository.findAll(); }
    @Override public List<Producto> listActivos()     { return productoRepository.findByEstadoTrue(); }
    @Override public void insert(Producto p)          { productoRepository.save(p); }
    @Override public Producto searchId(Long id)       { return productoRepository.findById(id).orElse(new Producto()); }
    @Override public void update(Producto p)          { productoRepository.save(p); }
    @Override public void delete(Long id)             { productoRepository.deleteById(id); }

    @Override
    public void cambiarEstado(Long id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setEstado(!Boolean.TRUE.equals(p.getEstado()));
            productoRepository.save(p);
        });
    }

    @Override
    public List<Producto> filtrar(Long categoriaId, Long marcaId,
                                  BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.filtrar(categoriaId, marcaId, precioMin, precioMax);
    }

    @Override
    public List<Producto> buscar(String q) {
        return productoRepository.searchByTexto(q);
    }

    @Override
    public List<Producto> comparar(List<Long> ids) {
        return productoRepository.findByIdIn(ids);
    }
}