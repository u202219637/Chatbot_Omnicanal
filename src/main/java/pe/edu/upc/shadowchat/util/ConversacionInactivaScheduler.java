package pe.edu.upc.shadowchat.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.edu.upc.shadowchat.repositories.ConversacionRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IConversacionService;

import java.util.List;

/**
 * Auto-cierre de conversaciones inactivas.
 *
 * Contexto: la omnicanalidad (HU20) busca siempre "la última conversación
 * ABIERTA o ESCALADA del usuario" para seguir el hilo desde cualquier canal.
 * Sin un mecanismo de cierre, una conversación ABIERTA se vuelve un agujero
 * negro permanente: cualquier consulta nueva del cliente —aunque sea sobre
 * un producto totalmente distinto— se sigue agregando al mismo hilo viejo.
 *
 * Este job corre cada hora y cierra automáticamente (como RESUELTA) cualquier
 * conversación ABIERTA cuyo último mensaje (o fecha de inicio, si nunca tuvo
 * mensajes) date de hace más de 24 horas. Esto imita el comportamiento
 * estándar de plataformas de mensajería (ej. la ventana de 24h de WhatsApp
 * Business API) y libera al usuario para iniciar una conversación nueva sin
 * que el bot mezcle contexto de productos distintos.
 *
 * Las conversaciones ESCALADAS (con un asesor humano asignado) NO se tocan
 * aquí — esas se cierran manualmente por el asesor vía el endpoint
 * PUT /chat/{id}/cerrar.
 */
@Component
public class ConversacionInactivaScheduler {

    private static final int HORAS_INACTIVIDAD_LIMITE = 24;

    @Autowired private ConversacionRepository conversacionRepository;
    @Autowired private IConversacionService conversacionService;

    // Corre cada hora, en el minuto 0. fixedRate también serviría, pero un
    // cron explícito es más predecible para logs/debugging en producción.
    @Scheduled(cron = "0 0 * * * *")
    public void cerrarConversacionesInactivas() {
        List<Long> idsInactivas = conversacionRepository
                .findIdsAbiertasInactivasDesdeHoras(HORAS_INACTIVIDAD_LIMITE);

        if (idsInactivas.isEmpty()) return;

        System.out.println("[ConversacionInactivaScheduler] Cerrando " +
                idsInactivas.size() + " conversación(es) ABIERTA inactivas desde hace " +
                HORAS_INACTIVIDAD_LIMITE + "h: " + idsInactivas);

        for (Long id : idsInactivas) {
            try {
                // Se marca como resuelta=true (auto-cierre por inactividad,
                // no es un abandono ni un fallo del bot).
                conversacionService.cerrar(id, true);
            } catch (Exception e) {
                System.err.println("[ConversacionInactivaScheduler] Error cerrando conversación " +
                        id + ": " + e.getMessage());
            }
        }
    }
}