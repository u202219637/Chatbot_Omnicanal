package pe.edu.upc.shadowchat.controllers;

import pe.edu.upc.shadowchat.serviceInterfaces.IFeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.dtos.dashboard.*;
import pe.edu.upc.shadowchat.serviceInterfaces.IConversacionService;
import pe.edu.upc.shadowchat.serviceInterfaces.IMensajeService;
import pe.edu.upc.shadowchat.dtos.dashboard.ConvPorDiaDTO;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    @Autowired private IConversacionService conversacionService;
    @Autowired private IMensajeService mensajeService;
    @Autowired private IFeedbackService feedbackService;

    // GET /admin/dashboard/kpis (HU25)
    @GetMapping("/kpis")
    public ResponseEntity<KpiDTO> kpis() {
        Object[] raw = conversacionService.kpisMes();
        KpiDTO d = new KpiDTO();
        if (raw != null && raw.length >= 4) {
            d.setTotalConversaciones(toLong(raw[0]));
            d.setTasaResolucionAutomatica(toDouble(raw[1]));
            long ms = toLong(raw[2]);
            d.setTiempoPromedioRespuesta(formatMs(ms));
            d.setSatisfaccionPromedio(toDouble(raw[3]));
        }
        // Canal breakdown
        conversacionService.countByOrigen().forEach(row -> {
            String origen = String.valueOf(row[0]);
            long total = toLong(row[1]);
            if ("WEB".equals(origen)) d.setConversacionesWeb(total);
            else if ("WHATSAPP".equals(origen)) d.setConversacionesWhatsapp(total);
        });
        // Tasa de desvío a asesor = % de conversaciones que tuvieron escalación
        long totalConv = d.getTotalConversaciones() != null ? d.getTotalConversaciones() : 0;
        long escaladas = conversacionService.countByFueEscaladaTrue();
        d.setTasaDesvioHumano(totalConv > 0 ? Math.round((escaladas * 1000.0 / totalConv)) / 10.0 : 0.0);
        return ResponseEntity.ok(d);
    }

    // GET /admin/dashboard/tokens (HU31)
    @GetMapping("/tokens")
    public List<TokenConsumoDTO> tokens() {
        return conversacionService.tokensPorDia().stream().map(row -> {
            TokenConsumoDTO d = new TokenConsumoDTO();
            d.setDia(String.valueOf(row[0]));
            d.setTokensEntrada(toLong(row[1]));
            d.setTokensSalida(toLong(row[2]));
            d.setTokensTotal(toLong(row[3]));
            return d;
        }).collect(Collectors.toList());
    }

    // GET /admin/dashboard/intenciones (HU26)
    @GetMapping("/intenciones")
    public List<IntencionFrecuenteDTO> intenciones() {
        List<Object[]> rows = conversacionService.topIntenciones();
        long total = rows.stream().mapToLong(r -> toLong(r[1])).sum();
        return rows.stream().map(row -> {
            IntencionFrecuenteDTO d = new IntencionFrecuenteDTO();
            d.setIntencion(String.valueOf(row[0]));
            d.setFrecuencia(toLong(row[1]));
            d.setPorcentaje(total > 0 ? (toLong(row[1]) * 100.0 / total) : 0.0);
            return d;
        }).collect(Collectors.toList());
    }

    // GET /admin/dashboard/documentos-usados (HU26)
    @GetMapping("/documentos-usados")
    public List<DocumentoUsadoDTO> documentosUsados() {
        return mensajeService.documentosMasUsados().stream().map(row -> {
            DocumentoUsadoDTO d = new DocumentoUsadoDTO();
            d.setTituloDocumento(String.valueOf(row[0]));
            d.setUsos(toLong(row[1]));
            return d;
        }).collect(Collectors.toList());
    }

    // ── helpers ──────────────────────────────────────────────────
    private Long toLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Number) return ((Number) o).longValue();
        try { return Long.parseLong(o.toString()); } catch (Exception e) { return 0L; }
    }
    private Double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }
    private String formatMs(long ms) {
        long min = ms / 60000;
        long sec = (ms % 60000) / 1000;
        return String.format("%02d:%02d", min, sec);
    }
    // GET /admin/dashboard/conversaciones-por-dia
    @GetMapping("/conversaciones-por-dia")
    public List<ConvPorDiaDTO> convsPorDia() {
        return conversacionService.convsPorDia().stream().map(row -> {
            ConvPorDiaDTO d = new ConvPorDiaDTO();
            d.setDia(String.valueOf(row[0]));
            d.setTotalWeb(toLong(row[1]));
            d.setTotalWhatsapp(toLong(row[2]));
            d.setTotal(toLong(row[1]) + toLong(row[2]));
            return d;
        }).collect(Collectors.toList());
    }

    @GetMapping("/comentarios")
    public List<ComentarioGlobalDTO> comentarios() {
        return feedbackService.comentariosRecientesGlobal().stream().map(row -> {
            ComentarioGlobalDTO d = new ComentarioGlobalDTO();
            d.setCalificacion(row[0] != null ? ((Number) row[0]).intValue() : null);
            d.setMotivo(row[1] != null ? row[1].toString() : null);
            d.setComentario(row[2] != null ? row[2].toString() : null);
            d.setFecha(row[3] != null ? ((java.sql.Timestamp) row[3]).toLocalDateTime() : null);
            String nombre = (row[4] != null ? row[4].toString() : "") +
                    " " + (row[5] != null ? row[5].toString() : "");
            d.setClienteNombre(nombre.trim());
            return d;
        }).collect(Collectors.toList());
    }

    @GetMapping("/palabras-frecuentes")
    public List<PalabraFrecuenteDTO> palabrasFrecuentes() {
        return feedbackService.palabrasFrecuentes().stream().map(row -> {
            PalabraFrecuenteDTO d = new PalabraFrecuenteDTO();
            d.setPalabra(String.valueOf(row[0]));
            d.setTotal(toLong(row[1]));
            return d;
        }).collect(Collectors.toList());
    }
}