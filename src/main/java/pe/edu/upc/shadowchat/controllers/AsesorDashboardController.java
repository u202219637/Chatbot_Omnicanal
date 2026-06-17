package pe.edu.upc.shadowchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.dtos.dashboard.SatisfaccionAsesorDTO;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.serviceInterfaces.IFeedbackService;
import pe.edu.upc.shadowchat.serviceInterfaces.IUsuarioService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// HU31 — Como asesor, quiero ver el dashboard de métricas,
// para visualizar la satisfacción de los clientes.
@RestController
@RequestMapping("/asesor/dashboard")
@PreAuthorize("hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
public class AsesorDashboardController {

    @Autowired private IFeedbackService feedbackService;
    @Autowired private IUsuarioService usuarioService;

    @GetMapping("/satisfaccion")
    public ResponseEntity<SatisfaccionAsesorDTO> satisfaccion() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario asesor = usuarioService.findByUsername(username);

        SatisfaccionAsesorDTO dto = new SatisfaccionAsesorDTO();

        List<SatisfaccionAsesorDTO.DistribucionItem> distribucion = new ArrayList<>();
        long totalCasos = 0;
        double sumaCalificaciones = 0;

        for (Object[] row : feedbackService.distribucionPorAsesor(asesor.getId())) {
            int estrellas = row[0] != null ? ((Number) row[0]).intValue() : 0;
            long total = toLong(row[1]);

            SatisfaccionAsesorDTO.DistribucionItem item = new SatisfaccionAsesorDTO.DistribucionItem();
            item.setEstrellas(estrellas);
            item.setTotal(total);
            distribucion.add(item);

            totalCasos += total;
            sumaCalificaciones += estrellas * total;
        }

        dto.setDistribucion(distribucion);
        dto.setTotalCasosCalificados(totalCasos);
        dto.setPromedioPropio(totalCasos > 0 ? sumaCalificaciones / totalCasos : 0.0);

        List<SatisfaccionAsesorDTO.ComentarioItem> comentarios = new ArrayList<>();
        for (Object[] row : feedbackService.comentariosRecientesPorAsesor(asesor.getId())) {
            SatisfaccionAsesorDTO.ComentarioItem item = new SatisfaccionAsesorDTO.ComentarioItem();
            item.setCalificacion(row[0] != null ? ((Number) row[0]).intValue() : null);
            item.setMotivo(row[1] != null ? row[1].toString() : null);
            item.setComentario(row[2] != null ? row[2].toString() : null);
            item.setFecha(row[3] != null ? ((java.sql.Timestamp) row[3]).toLocalDateTime() : null);
            String nombre = (row[4] != null ? row[4].toString() : "") +
                    " " + (row[5] != null ? row[5].toString() : "");
            item.setClienteNombre(nombre.trim());
            comentarios.add(item);
        }
        dto.setComentariosRecientes(comentarios);

        return ResponseEntity.ok(dto);
    }

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
}