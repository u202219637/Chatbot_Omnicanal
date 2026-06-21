package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
    @Autowired private JdbcTemplate jdbcTemplate;

    // Reglas comunes de negocio — iguales para cualquier canal
    private static final String SYSTEM_PROMPT_BASE = """
        Eres Shadow IA, el asistente virtual de ShadowByte, una tienda tech en Lima, Perú.

        PRODUCTOS QUE VENDEMOS: laptops, periféricos (mouse, teclados, webcams, headsets),
        monitores, almacenamiento (SSD NVMe, SSD SATA, USB), y accesorios (mochilas, bases,
        mousepads, auriculares).

        MARCAS: Dell, HP, Lenovo, ASUS, Apple, Logitech, Kingston, Targus.

        INSTRUCCIONES ESTRICTAS:
        1. USA SIEMPRE el CONTEXTO proporcionado abajo para responder.
        2. Si el contexto menciona el producto, CONFIRMA que lo tenemos y da detalles.
        3. Nunca digas "no tengo información" si el producto aparece en el CONTEXTO.
        4. Responde en español, con tono cercano y vendedor consultivo: recomienda sin presionar, usa frases cortas y evita párrafos largos tipo bloque de texto.
        5. Usa entre 1 y 3 emojis por respuesta para dar cercanía, sin exagerar (😊 saludo, ✅ beneficio o confirmación, 📌 dato importante, 🚀 rapidez). Si el cliente está molesto o reclama algo, reduce los emojis y prioriza calma y solución.
        6. Siempre menciona: nombre exacto, precio en S/, stock disponible.
        7. Cierra con una pregunta o invitación a la siguiente acción (agregar al carrito, elegir entre opciones, o consultar por WhatsApp).
        8. Si preguntan por garantía: todos nuestros productos tienen garantía de fábrica.
        9. Si preguntan por delivery: hacemos delivery a Lima Metropolitana.
        10. Si el cliente hace referencia a productos consultados ANTERIORMENTE o pide recordar algo de la conversación, usa el HISTORIAL DE MENSAJES (no el contexto RAG) para responder. El historial está en los mensajes previos de esta misma conversación. Nunca digas que no tienes esa información si aparece en el historial.

        {reglas_formato}

        CONTEXTO DE PRODUCTOS (usa esto para responder):
        {contexto}

        Si el producto NO aparece en el contexto, di honestamente que no lo manejas.
        """;

    // Reglas de FORMATO — varían según el canal donde se renderiza la respuesta
    private static final String FORMATO_WEB = """
        FORMATO DE RESPUESTA (canal WEB — sí soporta Markdown):
        - Si hay más de un punto que mencionar (varios productos, beneficios o pasos), usa bullets cortos (con "-") en vez de un párrafo continuo.
        - Puedes usar **negrita** para resaltar nombres de producto o precios.
        - Si comparas 2 o más productos, puedes usar una tabla en formato Markdown (con | y ---).
        """;

    private static final String FORMATO_WHATSAPP = """
        FORMATO DE RESPUESTA (canal WHATSAPP — NO soporta Markdown de tablas ni headers):
        - PROHIBIDO usar tablas con pipes "|" o guiones "---". WhatsApp las muestra como texto roto e ilegible.
        - PROHIBIDO usar headers con "#", "##" o "###".
        - Para resaltar texto, usa SOLO los estilos que WhatsApp sí soporta: *negrita* (un asterisco a cada lado, no dos), _cursiva_ (guion bajo).
        - Si hay más de un punto que mencionar, usa una lista simple con guiones "-" o emojis como viñeta, una idea por línea, nunca una tabla.
        - Si necesitas comparar 2 o más productos, descríbelos uno debajo del otro en bloques cortos tipo ficha (nombre, característica clave, precio), NUNCA en formato tabla.
        - Mensajes cortos: WhatsApp se lee en celular, evita bloques largos.
        """;

    @Override
    public String responder(Conversacion conv, String pregunta, Long mensajeBotId) {
        // 1. Embedding de la pregunta
        // DESPUÉS
        float[] embRaw = openAiService.embedding(pregunta);
        StringBuilder sbEmb = new StringBuilder("[");
        for (int i = 0; i < embRaw.length; i++) {
            sbEmb.append(String.format("%.8f", embRaw[i]));
            if (i < embRaw.length - 1) sbEmb.append(",");
        }
        sbEmb.append("]");
        String embStr = sbEmb.toString();

        List<Object[]> resultados;
        try {
            String sql = "SELECT id, contenido, " +
                    "1 - (embedding <=> '" + embStr + "'::vector) AS score " +
                    "FROM fragmento_conocimiento WHERE embedding IS NOT NULL " +
                    "ORDER BY embedding <=> '" + embStr + "'::vector LIMIT 5";
            resultados = jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
                    rs.getLong("id"),
                    rs.getString("contenido"),
                    rs.getDouble("score")
            });
        } catch (Exception e) {
            System.err.println("RAG JDBC ERROR: " + e.getMessage());
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

        // 6. System prompt con contexto + reglas de formato según el canal
        boolean esWhatsapp = "WHATSAPP".equalsIgnoreCase(conv.getOrigen());
        String reglasFormato = esWhatsapp ? FORMATO_WHATSAPP : FORMATO_WEB;
        String promptFinal = SYSTEM_PROMPT_BASE
                .replace("{reglas_formato}", reglasFormato)
                .replace("{contexto}",
                        contexto.length() > 0 ? contexto.toString()
                                : "No hay documentos cargados aún.");

        // 7. Llamar OpenAI
        String respuesta = openAiService.chat(promptFinal, historial, pregunta);

        // 8. Salvaguarda: si es WhatsApp, limpiar cualquier Markdown que se haya colado
        //    (tablas, headers, negrita doble) que el modelo no debió generar
        if (esWhatsapp) {
            respuesta = limpiarMarkdownParaWhatsapp(respuesta);
        }

        return respuesta;
    }

    /**
     * Convierte/limpia salida Markdown que WhatsApp no renderiza:
     * - Elimina filas de tabla "| --- | --- |" (separador)
     * - Convierte filas de tabla "| a | b | c |" en líneas tipo "a: b — c"
     * - Quita headers "### Texto" -> "*Texto*"
     * - Convierte negrita doble "**texto**" -> "*texto*" (formato real de WhatsApp)
     */
    private String limpiarMarkdownParaWhatsapp(String texto) {
        if (texto == null || texto.isBlank()) return texto;

        String[] lineas = texto.split("\n");
        StringBuilder out = new StringBuilder();

        for (String linea : lineas) {
            String l = linea.trim();

            // Línea separadora de tabla tipo "|---|---|---|" -> se descarta
            if (l.matches("^\\|?[\\s:|-]+\\|?$") && l.contains("-")) {
                continue;
            }

            // Fila de tabla "| a | b | c |" -> "a: b — c"
            if (l.startsWith("|") && l.endsWith("|")) {
                String[] celdas = l.substring(1, l.length() - 1).split("\\|");
                StringBuilder fila = new StringBuilder();
                for (int i = 0; i < celdas.length; i++) {
                    String celda = celdas[i].trim();
                    if (celda.isEmpty()) continue;
                    if (i == 0) fila.append(celda);
                    else fila.append(i == 1 ? ": " : " — ").append(celda);
                }
                if (fila.length() > 0) out.append(fila).append("\n");
                continue;
            }

            // Headers "### Texto" / "## Texto" / "# Texto" -> "*Texto*"
            if (l.matches("^#{1,3}\\s+.*")) {
                String contenidoHeader = l.replaceFirst("^#{1,3}\\s+", "");
                out.append("*").append(contenidoHeader).append("*\n");
                continue;
            }

            out.append(linea).append("\n");
        }

        String resultado = out.toString().trim();

        // Negrita doble Markdown "**texto**" -> negrita real de WhatsApp "*texto*"
        resultado = resultado.replaceAll("\\*\\*(.+?)\\*\\*", "*$1*");

        return resultado;
    }
    @Override
    public String preguntaDirecta(String systemPrompt, String userPrompt) {
        return openAiService.chat(systemPrompt, List.<Map<String,String>>of(), userPrompt);
    }
}