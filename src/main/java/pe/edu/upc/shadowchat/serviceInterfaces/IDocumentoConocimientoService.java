package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.DocumentoConocimiento;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDocumentoConocimientoService {

    // Listado y búsqueda (HU15, panel admin)
    List<DocumentoConocimiento> list();
    List<DocumentoConocimiento> listByEstado(String estado);
    DocumentoConocimiento searchId(Long id);

    /*
     * Carga completa del pipeline RAG (HU15):
     * 1. Sube el archivo a Azure Blob Storage
     * 2. Guarda el registro con estado PENDIENTE
     * 3. Extrae texto con Apache Tika
     * 4. Divide en chunks (~512 tokens)
     * 5. Genera embeddings con OpenAI
     * 6. Guarda fragmentos en fragmento_conocimiento
     * 7. Actualiza estado a PROCESADO (o ERROR si falla)
     *
     * La lógica real vive en RagService;
     * este método es el punto de entrada desde el Controller.
     */
    DocumentoConocimiento cargar(MultipartFile archivo, Long usuarioId,
                                 Long productoId, String tipoDocumento);

    // Reintentar procesamiento cuando estado = ERROR (HU33)
    void reprocesar(Long id);

    // Eliminar documento y todos sus fragmentos (HU29)
    void eliminar(Long id);

    // Estadísticas para el panel Base RAG (HU15)
    long countDocumentos();
    long countFragmentos();
    long sumTokens();
}