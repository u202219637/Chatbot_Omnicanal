package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.*;
import pe.edu.upc.shadowchat.repositories.*;
import pe.edu.upc.shadowchat.serviceInterfaces.IOpenAiService;
import pe.edu.upc.shadowchat.serviceInterfaces.IRagService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class RagServiceImplement implements IRagService {

    @Autowired private IOpenAiService openAiService;
    @Autowired private FragmentoConocimientoRepository fragmentoRepository;
    @Autowired private MensajeRepository mensajeRepository;
    @Autowired private FuenteRespuestaRepository fuenteRespuestaRepository;

    private static final String SYSTEM_PROMPT = """
    Eres Shadow IA, el asistente virtual de ShadowByte, una tienda tech en Lima, Perú.
    
    PRODUCTOS QUE VENDEMOS: laptops, periféricos (mouse, teclados, webcams, headsets),
    monitores, almacenamiento (SSD NVMe, SSD SATA, USB), y accesorios (mochilas, bases,
    mousepads, auriculares).
    
    MARCAS: Dell, HP, Lenovo, ASUS, Apple, Logitech, Kingston, Targus.
    
    INSTRUCCIONES ESTRICTAS:
    1. USA SIEMPRE el CONTEXTO proporcionado abajo para responder.
    2. Si el contexto menciona el producto, CONFIRMA que lo tenemos y da detalles.
    3. Nunca digas "no tengo información" si el producto aparece en el CONTEXTO.
    4. Responde en español, tono amigable y vendedor, máximo 4 oraciones.
    5. Siempre menciona: nombre exacto, precio en S/, stock disponible.
    6. Cierra animando a agregar al carrito o consultar por WhatsApp.
    7. Si preguntan por garantía: todos nuestros productos tienen garantía de fábrica.
    8. Si preguntan por delivery: hacemos delivery a Lima Metropolitana.
    9. Si el cliente hace referencia a productos consultados ANTERIORMENTE o pide recordar algo de la conversación, usa el HISTORIAL DE MENSAJES (no el contexto RAG) para responder. El historial está en los mensajes previos de esta misma conversación. Nunca digas que no tienes esa información si aparece en el historial.
    
    CONTEXTO DE PRODUCTOS (usa esto para responder):
    {contexto}
    
    Si el producto NO aparece en el contexto, di honestamente que no lo manejas.
    """;

    @Override
    public String responder(Conversacion conv, String pregunta, Long mensajeBotId) {
        // 1. Embedding de la pregunta
        // DESPUÉS
        float[] embRaw = openAiService.embedding(pregunta);
        String embedding = "[" + java.util.stream.IntStream.range(0, embRaw.length)
                .mapToObj(i -> String.valueOf(embRaw[i]))
                .collect(Collectors.joining(",")) + "]";

        List<Object[]> resultados;
        try {
            resultados = fragmentoRepository.findTopKWithScore(embedding, 5);
        } catch (Exception e) {
            resultados = new java.util.ArrayList<>();
        }

        // 3. Construir contexto — ignorar filas con contenido null
        StringBuilder contexto = new StringBuilder();
        for (Object[] fila : resultados) {
            if (fila == null || fila[1] == null) continue;
            String contenido = (String) fila[1];
            // Score puede ser null si no hay embedding — usar 0.5 como fallback
            Double score = fila[2] != null ? ((Number) fila[2]).doubleValue() : 0.5;
            contexto.append("---\n").append(contenido).append("\n");

            // Guardar fuente solo si hay score válido
            if (mensajeBotId != null && fila[0] != null) {
                try {
                    Long fragId = ((Number) fila[0]).longValue();
                    Mensaje msgBot = mensajeRepository.findById(mensajeBotId).orElse(null);
                    FragmentoConocimiento frag = fragmentoRepository.findById(fragId).orElse(null);
                    if (msgBot != null && frag != null) {
                        FuenteRespuesta fr = new FuenteRespuesta();
                        fr.setMensaje(msgBot);
                        fr.setFragmento(frag);
                        fr.setTipoFuente("RAG");
                        fr.setScoreRelevancia(BigDecimal.valueOf(score));
                        fuenteRespuestaRepository.save(fr);
                    }
                } catch (Exception ignored) {}
            }
        }

        // 4. Si no hay fragmentos del RAG, traer los primeros 5 directo
        if (contexto.length() == 0) {
            fragmentoRepository.findAll().stream().limit(5).forEach(f -> {
                if (f.getContenido() != null) {
                    contexto.append("---\n").append(f.getContenido()).append("\n");
                }
            });
        }

        // 5. Historial reciente
        List<Mensaje> historialMensajes = mensajeRepository
                .findTop12ByConversacionIdOrderByFechaEnvioAsc(conv.getId());
        List<Map<String, String>> historial = historialMensajes.stream()
                .filter(m -> "CLIENTE".equals(m.getTipoEmisor()) || "BOT".equals(m.getTipoEmisor()))
                .map(m -> Map.of(
                        "role",    "CLIENTE".equals(m.getTipoEmisor()) ? "user" : "assistant",
                        "content", m.getContenido() != null ? m.getContenido() : ""
                ))
                .collect(Collectors.toList());

        // 6. System prompt con contexto
        String promptFinal = SYSTEM_PROMPT.replace("{contexto}",
                contexto.length() > 0 ? contexto.toString()
                        : "No hay documentos cargados aún.");

        // 7. Llamar OpenAI
        return openAiService.chat(promptFinal, historial, pregunta);
    }
    @Override
    public String preguntaDirecta(String systemPrompt, String userPrompt) {
        return openAiService.chat(systemPrompt, List.<Map<String,String>>of(), userPrompt);
    }
}