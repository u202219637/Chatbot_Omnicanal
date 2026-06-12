package pe.edu.upc.shadowchat.serviceImplements;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upc.shadowchat.entities.DocumentoConocimiento;
import pe.edu.upc.shadowchat.entities.FragmentoConocimiento;
import pe.edu.upc.shadowchat.entities.Producto;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.repositories.DocumentoConocimientoRepository;
import pe.edu.upc.shadowchat.repositories.FragmentoConocimientoRepository;
import pe.edu.upc.shadowchat.repositories.ProductoRepository;
import pe.edu.upc.shadowchat.repositories.UsuarioRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IDocumentoConocimientoService;
import pe.edu.upc.shadowchat.serviceInterfaces.IOpenAiService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentoConocimientoServiceImplement implements IDocumentoConocimientoService {

    @Autowired private DocumentoConocimientoRepository documentoRepository;
    @Autowired private FragmentoConocimientoRepository fragmentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private IOpenAiService openAiService;

    // ── Listado ──────────────────────────────────────────────────────────────

    @Override
    public List<DocumentoConocimiento> list() {
        return documentoRepository.findAllByOrderByFechaCargaDesc();
    }

    @Override
    public List<DocumentoConocimiento> listByEstado(String estado) {
        return documentoRepository.findByEstadoOrderByFechaCargaDesc(estado);
    }

    @Override
    public DocumentoConocimiento searchId(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + id));
    }

    // ── Pipeline RAG completo ────────────────────────────────────────────────

    @Override
    public DocumentoConocimiento cargar(MultipartFile archivo, Long usuarioId,
                                        Long productoId, String tipoDocumento) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        Producto producto = (productoId != null)
                ? productoRepository.findById(productoId).orElse(null)
                : null;

        // 1. Registro inicial
        DocumentoConocimiento doc = new DocumentoConocimiento();
        doc.setTitulo(archivo.getOriginalFilename());
        doc.setTipoDocumento(tipoDocumento != null ? tipoDocumento : "DOC");
        doc.setEstado("PENDIENTE");
        doc.setUsuarioCarga(usuario);
        doc.setProducto(producto);
        documentoRepository.save(doc);

        try {
            // 2. Extraer texto con Tika (DOCX, PDF, TXT)
            Tika tika = new Tika();
            String textoCompleto = tika.parseToString(archivo.getInputStream());

            if (textoCompleto == null || textoCompleto.isBlank()) {
                System.err.println("[RAG] Tika no extrajo texto de: " + archivo.getOriginalFilename());
                doc.setEstado("ERROR");
                return documentoRepository.save(doc);
            }

            System.out.println("[RAG] Texto extraído: " + textoCompleto.length() + " chars de " + archivo.getOriginalFilename());

            // 3. Chunking — fragmentos de ~500 chars con overlap de 50
            List<String> chunks = chunkTexto(textoCompleto, 500, 50);
            System.out.println("[RAG] Chunks generados: " + chunks.size());

            // 4. Generar embedding para cada chunk → guardar en pgvector
            int orden = 1;
            int errores = 0;
            for (String chunk : chunks) {
                try {
                    float[] embedding = openAiService.embedding(chunk);
                    FragmentoConocimiento frag = new FragmentoConocimiento();
                    frag.setDocumentoConocimiento(doc);
                    frag.setContenido(chunk);
                    frag.setOrdenFragmento(orden++);
                    frag.setEmbedding(embedding);
                    frag.setCantidadTokens(chunk.split("\\s+").length);
                    frag.setEstado(true);
                    fragmentoRepository.save(frag);
                } catch (Exception e) {
                    System.err.println("[RAG] Error embedding chunk " + orden + ": " + e.getMessage());
                    errores++;
                }
            }

            System.out.println("[RAG] Fragmentos guardados: " + (orden - 1) + " | Errores: " + errores);

            doc.setEstado("PROCESADO");
            doc.setFechaProcesamiento(LocalDateTime.now());

        } catch (Exception ex) {
            ex.printStackTrace();
            doc.setEstado("ERROR");
        }

        return documentoRepository.save(doc);
    }

    @Override
    public void reprocesar(Long id) {
        // Borra fragmentos existentes y vuelve a PENDIENTE
        // El admin debe re-subir el archivo para regenerar embeddings
        fragmentoRepository.deleteByDocumentoConocimientoId(id);
        DocumentoConocimiento doc = searchId(id);
        doc.setEstado("PENDIENTE");
        documentoRepository.save(doc);
    }

    @Override
    public void eliminar(Long id) {
        fragmentoRepository.deleteByDocumentoConocimientoId(id);
        documentoRepository.deleteById(id);
    }

    // ── Estadísticas ─────────────────────────────────────────────────────────

    @Override
    public long countDocumentos() {
        return documentoRepository.count();
    }

    @Override
    public long countFragmentos() {
        return fragmentoRepository.count();
    }

    @Override
    public long sumTokens() {
        return fragmentoRepository.findAll().stream()
                .mapToLong(f -> f.getCantidadTokens() != null ? f.getCantidadTokens() : 0L)
                .sum();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private List<String> chunkTexto(String texto, int tamano, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (texto == null || texto.isBlank()) return chunks;

        int inicio = 0;
        int largo = texto.length();

        while (inicio < largo) {
            int fin = Math.min(inicio + tamano, largo);
            chunks.add(texto.substring(inicio, fin));
            inicio += tamano - overlap;
            // Protección contra bucle infinito
            if (tamano <= overlap) break;
        }
        return chunks;
    }
}