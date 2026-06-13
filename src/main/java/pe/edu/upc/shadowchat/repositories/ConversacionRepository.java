package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.Conversacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {

    // Historial del cliente (HU18) — ordenado por más reciente
    List<Conversacion> findByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);

    // Conversación activa del usuario en un canal (HU13, HU20)
    Optional<Conversacion> findByUsuarioIdAndOrigenAndEstado(
            Long usuarioId, String origen, String estado);

    // ----------------------------------------------------------------
    // KPIs del mes — dashboard admin (HU25)
    // Retorna: [total, tasa_resolucion_pct, ms_promedio, satisfaccion_avg]
    // ----------------------------------------------------------------
    @Query(value = """
            SELECT
                COUNT(*)                                               AS total_consultas,
                ROUND(AVG(CASE WHEN fue_resuelta THEN 100.0 END), 1) AS tasa_resolucion_pct,
                ROUND(AVG(tiempo_promedio_respuesta_ms))              AS ms_promedio,
                ROUND(AVG(satisfaccion), 1)                           AS satisfaccion_promedio
            FROM conversacion
            WHERE fecha_inicio >= NOW() - INTERVAL '30 days'
            """, nativeQuery = true)
    Object[] kpisMes();

    // Consultas frecuentes por intención detectada (HU26)
    @Query(value = """
            SELECT m.intencion_detectada, COUNT(*) AS frecuencia
            FROM mensaje m
            WHERE m.tipo_emisor = 'CLIENTE'
              AND m.intencion_detectada IS NOT NULL
            GROUP BY m.intencion_detectada
            ORDER BY frecuencia DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> topIntenciones();

    // Consumo de tokens acumulado por día (HU31)
    @Query(value = """
            SELECT
                DATE(m.fecha_envio)         AS dia,
                SUM(m.tokens_entrada)       AS tokens_entrada,
                SUM(m.tokens_salida)        AS tokens_salida,
                SUM(m.tokens_entrada + m.tokens_salida) AS tokens_total
            FROM mensaje m
            WHERE m.tipo_emisor = 'BOT'
              AND m.fecha_envio >= NOW() - INTERVAL '30 days'
            GROUP BY dia
            ORDER BY dia
            """, nativeQuery = true)
    List<Object[]> tokensPorDia();

    // Conversaciones por canal (HU25)
    @Query(value = """
            SELECT origen, COUNT(*) AS total
            FROM conversacion
            WHERE fecha_inicio >= NOW() - INTERVAL '30 days'
            GROUP BY origen
            """, nativeQuery = true)
    List<Object[]> countByOrigen();

    // Escaladas activas (ASESOR — HU22)
    List<Conversacion> findByEstadoOrderByFechaInicioAsc(String estado);

    // Para informe de período personalizado (HU26)
    @Query(value = """
            SELECT COUNT(*) AS total,
                   ROUND(AVG(CASE WHEN fue_resuelta THEN 100.0 END), 1),
                   ROUND(AVG(tiempo_promedio_respuesta_ms)),
                   ROUND(AVG(satisfaccion), 1)
            FROM conversacion
            WHERE fecha_inicio BETWEEN :desde AND :hasta
            """, nativeQuery = true)
    Object[] kpisPeriodo(@Param("desde") String desde,
                         @Param("hasta") String hasta);

    // Omnicanalidad (HU20)
    Optional<Conversacion> findFirstByUsuarioIdAndEstadoOrderByFechaInicioDesc(
            Long usuarioId, String estado);

    Optional<Conversacion> findFirstByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);
    @Query("SELECT CAST(c.fechaInicio AS date), " +
            "SUM(CASE WHEN c.origen = 'WEB' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.origen = 'WHATSAPP' THEN 1 ELSE 0 END) " +
            "FROM Conversacion c " +
            "GROUP BY CAST(c.fechaInicio AS date) " +
            "ORDER BY CAST(c.fechaInicio AS date) ASC")

    List<Object[]> convsPorDia();

    // Filtros admin/asesor (HU22, HU25)
    @Query("""
    SELECT c FROM Conversacion c
    WHERE (:estado IS NULL OR c.estado = :estado)
      AND (:origen IS NULL OR c.origen = :origen)
      AND (:desde IS NULL OR c.fechaInicio >= :desde)
      AND (:hasta IS NULL OR c.fechaInicio < :hasta)
    ORDER BY c.fechaInicio DESC
    """)
    List<Conversacion> findAllWithFilters(
            @Param("estado") String estado,
            @Param("origen") String origen,
            @Param("desde") LocalDateTime desde,
            @Param("hasta")  LocalDateTime hasta);

}