package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Conversacion;

public interface IRagService {
    String responder(Conversacion conv, String pregunta, Long mensajeBotId);
    String preguntaDirecta(String systemPrompt, String userPrompt);
}