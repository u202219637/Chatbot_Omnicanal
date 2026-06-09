package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.upc.shadowchat.serviceInterfaces.IOpenAiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiServiceImplement implements IOpenAiService {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String chatModel;

    @Value("${openai.embedding.model:text-embedding-ada-002}")
    private String embeddingModel;

    public OpenAiServiceImplement(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.openai.com")
                .build();
    }

    @Override
    public float[] embedding(String texto) {
        Map response = webClient.post()
                .uri("/v1/embeddings")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of("model", embeddingModel, "input", texto))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        List<Double> vector = (List<Double>) data.get(0).get("embedding");

        float[] result = new float[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            result[i] = vector.get(i).floatValue();
        }
        return result;
    }

    @Override
    public String chat(String systemPrompt, List<String> historial, String mensajeUsuario) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        for (int i = 0; i < historial.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            messages.add(Map.of("role", role, "content", historial.get(i)));
        }
        messages.add(Map.of("role", "user", "content", mensajeUsuario));

        Map response = webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "model", chatModel,
                        "messages", messages,
                        "temperature", 0.2,
                        "max_tokens", 500
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}