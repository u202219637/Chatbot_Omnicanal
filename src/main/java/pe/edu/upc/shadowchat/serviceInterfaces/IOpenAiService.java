package pe.edu.upc.shadowchat.serviceInterfaces;

import java.util.List;

public interface IOpenAiService {
    float[] embedding(String texto);
    String chat(String systemPrompt, List<String> historial, String mensajeUsuario);
}
