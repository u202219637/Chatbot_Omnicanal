package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.FragmentoConocimiento;

import java.util.List;

public interface IFragmentoConocimientoService {

    // Preview de fragmentos de un documento (HU30)
    List<FragmentoConocimiento> listByDocumento(Long documentoId);
    long countByDocumento(Long documentoId);

    /*
     * Búsqueda semántica — núcleo del RAG (HU13, HU16, HU17):
     * Convierte el embedding de la pregunta del usuario en un vector
     * y retorna los topK fragmentos más similares por coseno.
     * Retorna Object[] = [id, contenido, score] para guardar en fuente_respuesta.
     */
    List<Object[]> buscarSimilares(float[] embedding, int topK);

    // Elimina todos los fragmentos de un documento (llamado desde IDocumentoService.eliminar)
    void deleteByDocumento(Long documentoId);
}