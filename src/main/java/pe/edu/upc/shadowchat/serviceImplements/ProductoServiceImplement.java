package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.*;
import pe.edu.upc.shadowchat.repositories.*;
import pe.edu.upc.shadowchat.serviceInterfaces.IOpenAiService;
import pe.edu.upc.shadowchat.serviceInterfaces.IProductoService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoServiceImplement implements IProductoService {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private FragmentoConocimientoRepository fragmentoRepository;
    @Autowired private DocumentoConocimientoRepository docRepository;
    @Autowired private IOpenAiService openAiService;

    @Override public List<Producto> list()        { return productoRepository.findAll(); }
    @Override public List<Producto> listActivos() { return productoRepository.findByEstadoTrue(); }
    @Override public Producto searchId(Long id)   { return productoRepository.findById(id).orElse(new Producto()); }
    @Override public void delete(Long id)         { productoRepository.deleteById(id); }

    @Override
    public void insert(Producto p) {
        productoRepository.save(p);
        generarEmbedding(p);  // auto-embedding al crear
    }

    @Override
    public void update(Producto p) {
        productoRepository.save(p);
        generarEmbedding(p);  // auto-embedding al editar
    }

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

    // ── AUTO-EMBEDDING ────────────────────────────────────────────────────
    private void generarEmbedding(Producto p) {
        try {
            // Buscar documento padre "Catálogo ShadowByte" (id=1)
            DocumentoConocimiento doc = docRepository.findById(2L).orElse(null);
            if (doc == null) {
                System.out.println("Warning: no existe documento_conocimiento con id=1");
                return;
            }

            // Construir texto representativo del producto
            String texto = "Producto: " + p.getNombre()
                    + " | Categoría: " + (p.getCategoria() != null ? p.getCategoria().getNombre() : "")
                    + " | Marca: " + (p.getMarca() != null ? p.getMarca().getNombre() : "")
                    + " | Precio: S/ " + p.getPrecio()
                    + " | Stock: " + p.getStock() + " unidades"
                    + " | Descripción: " + (p.getDescripcion() != null ? p.getDescripcion() : "")
                    + " | Especificaciones: " + (p.getEspecificaciones() != null ? p.getEspecificaciones() : "");

            // Generar embedding con OpenAI
            float[] embedding = openAiService.embedding(texto);


            // Borrar solo el fragmento de ESTE producto si ya existe (por nombre)
            fragmentoRepository.deleteByContenidoContaining("Producto: " + p.getNombre());

            // Guardar nuevo fragmento con embedding real
            FragmentoConocimiento frag = new FragmentoConocimiento();
            frag.setDocumentoConocimiento(doc);
            frag.setContenido(texto);
            frag.setEmbedding(embedding);
            frag.setOrdenFragmento(p.getId() != null ? p.getId().intValue() : 0);
            frag.setCantidadTokens(texto.length() / 4);
            frag.setEstado(true);
            fragmentoRepository.save(frag);

            System.out.println("✅ Embedding generado para: " + p.getNombre());
            // DESPUÉS — temporal para diagnosticar
        } catch (Exception e) {
            System.out.println("⚠️ ERROR REAL para " + p.getNombre() + ": " + e.getClass().getSimpleName() + " — " + e.getMessage());
            e.printStackTrace();
        }
    }
}