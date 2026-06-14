package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Mensaje;

import java.util.List;

public interface IMensajeService {

    // CRUD base
    void insert(Mensaje mensaje);
    Mensaje searchId(Long id);

    // Todos los mensajes de una conversación en orden cronológico (HU13, HU18)
    List<Mensaje> listByConversacion(Long conversacionId);

    // Documentos más consultados por el RAG (HU26)
    List<Object[]> documentosMasUsados();

    // Total de tokens de una conversación (HU31)
    Object[] sumaTokens(Long conversacionId);
    long countByConversacionIdAndTipoEmisor(Long conversacionId, String tipoEmisor);
}