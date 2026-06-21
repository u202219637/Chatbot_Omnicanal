package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.FuenteRespuesta;

import java.util.List;

public interface IFuenteRespuestaService {

    // Guarda las fuentes RAG de un mensaje (llamado desde RagService)
    void insert(FuenteRespuesta fuenteRespuesta);

    // Retorna las fuentes de un mensaje para mostrarlas en el chat (HU16)
    List<FuenteRespuesta> listByMensaje(Long mensajeId);

    // FIX rendimiento: trae las fuentes de varios mensajes en una sola query,
    // usado por GET /chat/{id}/mensajes para evitar N+1 (ver ConversacionController).
    List<FuenteRespuesta> listByMensajes(List<Long> mensajeIds);
}