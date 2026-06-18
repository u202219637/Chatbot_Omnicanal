package pe.edu.upc.shadowchat.serviceInterfaces;

import org.springframework.data.jpa.repository.Query;
import pe.edu.upc.shadowchat.entities.Conversacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IConversacionService {

    // CRUD base
    void insert(Conversacion conversacion);
    Conversacion searchId(Long id);
    void update(Conversacion conversacion);

    // Historial del cliente (HU18)
    List<Conversacion> listByUsuario(Long usuarioId);

    /*
     * Recupera la conversación ABIERTA de un usuario en un canal.
     * Si no existe, devuelve Optional.empty() y el servicio creará una nueva.
     * Usado en el flujo de chat (HU13) y en el webhook de Twilio (HU20).
     */
    Optional<Conversacion> findActiva(Long usuarioId, String origen);

    // Cierra la conversación y actualiza métricas finales
    void cerrar(Long id, Boolean fueResuelta);

    // Marca como escalada (llamado desde EscalacionService)
    void marcarEscalada(Long id);

    // KPIs para dashboard admin (HU25)
    Object[] kpisMes();
    Object[] kpisPeriodo(String desde, String hasta);
    List<Object[]> topIntenciones();
    List<Object[]> tokensPorDia();
    List<Object[]> countByOrigen();

    // Omnicanalidad — busca activa sin filtrar canal (HU20)
    Optional<Conversacion> findActivaByUsuario(Long usuarioId);

    List<Object[]> convsPorDia();

    // Admin/Asesor — lista todas con filtros (HU22, HU25)
    List<Conversacion> listAll(String estado, String origen,
                               LocalDateTime desde, LocalDateTime hasta);

    Optional<Conversacion> findMasRecienteByUsuario(Long usuarioId);
    Optional<Conversacion> findActivaOEscaladaByUsuario(Long usuarioId);
    long countByFueEscaladaTrue();

}