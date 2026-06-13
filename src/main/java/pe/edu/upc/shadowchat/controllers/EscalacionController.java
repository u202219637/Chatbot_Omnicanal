package pe.edu.upc.shadowchat.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.dtos.escalacion.*;
import pe.edu.upc.shadowchat.entities.Escalacion;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.serviceInterfaces.IConversacionService;
import pe.edu.upc.shadowchat.serviceInterfaces.IEscalacionService;
import pe.edu.upc.shadowchat.serviceInterfaces.IUsuarioService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/escalaciones")
@SecurityRequirement(name = "bearerAuth")
public class EscalacionController {

    @Autowired private IEscalacionService escalacionService;
    @Autowired private IUsuarioService usuarioService;
    @Autowired private IConversacionService conversacionService;

    // ── NUEVO: cliente solicita escalación desde el chat (HU22) ──────────────
    @PostMapping("/solicitar")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> solicitar(
            @RequestBody Map<String, String> body) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);

        // Busca la conversación activa del usuario
        var convOpt = conversacionService.findActivaByUsuario(usuario.getId());
        if (convOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No tienes una conversación activa."));
        }

        Long conversacionId = convOpt.get().getId();
        String motivo    = body.getOrDefault("motivo", "Solicitud del cliente");
        String prioridad = body.getOrDefault("prioridad", "MEDIA");

        Escalacion e = escalacionService.crear(conversacionId, motivo, prioridad);

        return ResponseEntity.ok(Map.of(
                "escalacionId",   e.getId(),
                "conversacionId", conversacionId,
                "estado",         e.getEstado(),
                "mensaje",        "Tu consulta fue escalada. Un asesor te atenderá pronto."
        ));
    }

    // GET /escalaciones (HU22 — asesor y admin)
    @GetMapping
    @PreAuthorize("hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public List<EscalacionListDTO> listar() {
        return escalacionService.listActivas().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    // GET /escalaciones/resumen (HU22, HU25)
    @GetMapping("/resumen")
    @PreAuthorize("hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<EscalacionResumenDTO> resumen() {
        EscalacionResumenDTO d = new EscalacionResumenDTO();
        d.setPendientes(escalacionService.countByEstado("PENDIENTE"));
        d.setEnRevision(escalacionService.countByEstado("ASIGNADA")
                + escalacionService.countByEstado("EN_ATENCION"));
        d.setResueltas(escalacionService.countByEstado("RESUELTA"));
        d.setTotal(d.getPendientes() + d.getEnRevision() + d.getResueltas());
        return ResponseEntity.ok(d);
    }

    // GET /escalaciones/mis-escalaciones (asesor ve las suyas)
    @GetMapping("/mis-escalaciones")
    @PreAuthorize("hasAuthority('ASESOR')")
    public List<EscalacionListDTO> misEscalaciones() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario asesor = usuarioService.findByUsername(username);
        return escalacionService.listByAsesor(asesor.getId(), "EN_ATENCION")
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // PUT /escalaciones/{id}/asignar (HU22)
    @PutMapping("/{id}/asignar")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> asignar(@PathVariable Long id,
                                        @RequestBody AsignarAsesorDTO dto) {
        escalacionService.asignar(id, dto.getAsesorId());
        return ResponseEntity.ok().build();
    }

    // PUT /escalaciones/{id}/iniciar (asesor toma el caso)
    @PutMapping("/{id}/iniciar")
    @PreAuthorize("hasAuthority('ASESOR')")
    public ResponseEntity<Void> iniciar(@PathVariable Long id) {
        escalacionService.iniciarAtencion(id);
        return ResponseEntity.ok().build();
    }

    // PUT /escalaciones/{id}/cerrar (HU22)
    @PutMapping("/{id}/cerrar")
    @PreAuthorize("hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> cerrar(@PathVariable Long id) {
        escalacionService.cerrar(id);
        return ResponseEntity.ok().build();
    }

    private EscalacionListDTO toDTO(Escalacion e) {
        EscalacionListDTO d = new EscalacionListDTO();
        d.setId(e.getId());
        d.setMotivo(e.getMotivo());
        d.setPrioridad(e.getPrioridad());
        d.setEstado(e.getEstado());
        d.setFechaCreacion(e.getFechaCreacion());
        d.setFechaAsignacion(e.getFechaAsignacion());
        d.setFechaCierre(e.getFechaCierre());
        if (e.getConversacion() != null) {
            d.setConversacionId(e.getConversacion().getId());
            if (e.getConversacion().getUsuario() != null) {
                Usuario u = e.getConversacion().getUsuario();
                d.setClienteNombre(u.getNombres() + " " + u.getApellidos());
                d.setClienteUsername(u.getUsername());
            }
        }
        if (e.getAsesor() != null)
            d.setAsesorNombre(e.getAsesor().getNombres() + " " + e.getAsesor().getApellidos());
        return d;
    }
}