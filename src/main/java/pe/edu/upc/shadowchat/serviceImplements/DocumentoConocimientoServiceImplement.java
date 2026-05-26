package pe.edu.upc.shadowchat.serviceImplements;

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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Orquesta el pipeline RAG completo (HU15):
 *   upload → Blob → Tika → chunk → OpenAI Embeddings → pgvector
 *
 * RagService, OpenAiService y AzureBlobService se inyectan aquí.
 * Se declaran como Object para no bloquear la compilación mientras
 * esos servicios aún no están implementados; cuando los crees
 * reemplaza Object por el tipo correcto y descomenta las llamadas.
 */
@Service
public class DocumentoConocimientoServiceImplement implements IDocumentoConocimientoService {

    @Autowired private DocumentoConocimientoRepository documentoRepository;
    @Autowired private FragmentoConocimientoRepository fragmentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;

    /*
     * TODO Sprint 2: inyectar cuando estén listos
     * @Autowired private RagService ragService;
     * @Autowired private AzureBlobService azureBlobService;
     */

    // ── Listado ─────────────────────────────────────────────────────────────

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

    // ── Pipeline RAG ─────────────────────────────────────────────────────────

    @Override
    public DocumentoConocimiento cargar(MultipartFile archivo, Long usuarioId,
                                        Long productoId, String tipoDocumento) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        Producto producto = (productoId != null)
                ? productoRepository.findById(productoId).orElse(null)
                : null;

        // 1. Registro inicial con estado PENDIENTE
        DocumentoConocimiento doc = new DocumentoConocimiento();
        doc.setTitulo(archivo.getOriginalFilename());
        doc.setTipoDocumento(tipoDocumento);
        doc.setEstado("PENDIENTE");
        doc.setUsuarioCarga(usuario);
        doc.setProducto(producto);
        documentoRepository.save(doc);

        try {
            // 2. TODO: subir a Azure Blob Storage
            // String urlBlob = azureBlobService.upload(archivo);
            // doc.setUrlBlob(urlBlob);

            // 3. TODO: pipeline Tika + chunking + embeddings + pgvector
            // ragService.procesar(doc, archivo);

            doc.setEstado("PROCESADO");
            doc.setFechaProcesamiento(LocalDateTime.now());
        } catch (Exception ex) {
            doc.setEstado("ERROR");
            // Puedes guardar el mensaje en un campo 'errorDetalle' si lo añades a la entidad
        }

        return documentoRepository.save(doc);
    }

    @Override
    public void reprocesar(Long id) {
        DocumentoConocimiento doc = searchId(id);
        doc.setEstado("PENDIENTE");
        documentoRepository.save(doc);
        // TODO: ragService.procesar(doc, archivo) — necesitas guardar el blob y releerlo
    }

    @Override
    public void eliminar(Long id) {
        // 1. Borra fragmentos (ON CASCADE no siempre funciona con pgvector)
        fragmentoRepository.deleteByDocumentoConocimientoId(id);
        // 2. Borra el documento
        documentoRepository.deleteById(id);
        // TODO: azureBlobService.delete(doc.getUrlBlob())
    }

    // ── Estadísticas panel RAG ───────────────────────────────────────────────

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
        // Suma tokens de todos los fragmentos
        return fragmentoRepository.findAll().stream()
                .mapToLong(f -> f.getCantidadTokens() != null ? f.getCantidadTokens() : 0L)
                .sum();
    }
}