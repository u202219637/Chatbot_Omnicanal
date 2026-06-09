package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.*;
import pe.edu.upc.shadowchat.repositories.*;
import pe.edu.upc.shadowchat.serviceInterfaces.IOpenAiService;
import pe.edu.upc.shadowchat.serviceInterfaces.IRagService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagServiceImplement implements IRagService {

    @Autowired private IOpenAiService openAiService;
    @Autowired private FragmentoConocimientoRepository fragmentoRepository;
    @Autowired private MensajeRepository mensajeRepository;
    @Autowired private FuenteRespuestaRepository fuenteRespuestaRepository;

    private static final String SYSTEM_PROMPT = """
        Eres Shadow IA, el asistente virtual de ShadowByte, una tienda tech en Lima, Perú.
        Vendes laptops, periféricos, monitores, almacenamiento y accesorios tecnológicos.
        
        INSTRUCCIONES:
        - Responde SIEMPRE en español, de forma amigable y concisa (máximo 4 oraciones).
        - Usa el CONTEXTO para responder. Si no está en el contexto, dilo honestamente.
        - Cuando el cliente mencione un producto: destaca ventajas, precio y disponibilidad.
        - Anima al cliente a agregar al carrito o consultar por WhatsApp.
        - Si no puedes resolver la consulta, ofrece escalar con un asesor humano.
        - NO inventes precios ni especificaciones que no estén en el contexto.
        
        CONTEXTO:
        {contexto}
        """;

    @Override
    public String responder(Conversacion conv, String pregunta, Long mensajeBotId) {
        // 1. Embedding de la pregunta
        float[] embedding = openAiService.embedding(pregunta);

        // 2. Buscar fragmentos con manejo de nulls en score
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
                .findTop6ByConversacionIdOrderByFechaEnvioAsc(conv.getId());
        List<String> historial = historialMensajes.stream()
                .map(Mensaje::getContenido)
                .collect(java.util.stream.Collectors.toList());

        // 6. System prompt con contexto
        String promptFinal = SYSTEM_PROMPT.replace("{contexto}",
                contexto.length() > 0 ? contexto.toString()
                        : "No hay documentos cargados aún.");

        // 7. Llamar OpenAI
        return openAiService.chat(promptFinal, historial, pregunta);
    }
}