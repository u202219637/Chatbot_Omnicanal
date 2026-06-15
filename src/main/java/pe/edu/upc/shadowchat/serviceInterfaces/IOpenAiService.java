package pe.edu.upc.shadowchat.serviceInterfaces;

import java.util.List;
import java.util.Map;
public interface IOpenAiService {
    float[] embedding(String texto);
    String chat(String systemPrompt, List<Map<String, String>> historial, String mensajeUsuario);
}
