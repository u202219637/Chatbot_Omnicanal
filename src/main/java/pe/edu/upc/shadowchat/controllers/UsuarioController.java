package pe.edu.upc.shadowchat.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.dtos.usuario.*;
import pe.edu.upc.shadowchat.dtos.canal.UsuarioCanalDTO;
import pe.edu.upc.shadowchat.entities.Rol;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.entities.UsuarioCanal;
import pe.edu.upc.shadowchat.serviceInterfaces.IUsuarioService;
import pe.edu.upc.shadowchat.serviceInterfaces.IRolService;
import pe.edu.upc.shadowchat.serviceInterfaces.IUsuarioCanalService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    @Autowired private IUsuarioService usuarioService;
    @Autowired private IRolService rolService;
    @Autowired private IUsuarioCanalService usuarioCanalService;

    // POST /usuarios — registro público, sin JWT (HU02)
    @PostMapping
    public ResponseEntity<Void> registrar(@RequestBody UsuarioRegistroDTO dto) {
        Usuario u = new Usuario();
        u.setNombres(dto.getNombres());
        u.setApellidos(dto.getApellidos());
        u.setCorreo(dto.getCorreo());
        u.setTelefono(dto.getTelefono());
        u.setUsername(dto.getUsername());
        u.setPasswordHash(dto.getPassword());
        usuarioService.insert(u);

        // Solo crear canal WHATSAPP si tiene teléfono
        if (dto.getTelefono() != null && !dto.getTelefono().isBlank()) {
            try {
                String tel = dto.getTelefono().trim();
                if (!tel.startsWith("+")) tel = "+51" + tel;
                usuarioCanalService.vincular(u.getId(), "WHATSAPP",
                        tel, dto.getNombres() + " " + dto.getApellidos());
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok().build();
    }

    // GET /usuarios/miperfil (HU05)
    @GetMapping({"/miperfil", "/me"})
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<UsuarioPerfilDTO> miPerfil() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioService.findByUsername(username);
        UsuarioPerfilDTO dto = new UsuarioPerfilDTO();
        dto.setId(u.getId());
        dto.setNombres(u.getNombres());
        dto.setApellidos(u.getApellidos());
        dto.setCorreo(u.getCorreo());
        dto.setTelefono(u.getTelefono());
        dto.setUsername(u.getUsername());
        dto.setEstado(u.getEstado());
        dto.setFechaRegistro(u.getFechaRegistro());
        dto.setUltimoAcceso(u.getUltimoAcceso());
        dto.setRoles(u.getRoles().stream().map(Rol::getRol).collect(Collectors.toList()));
        return ResponseEntity.ok(dto);
    }

    // PUT /usuarios/miperfil (HU05)
    @PutMapping({"/miperfil", "/me"})
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ASESOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> actualizarPerfil(@RequestBody UsuarioPerfilDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioService.findByUsername(username);
        u.setNombres(dto.getNombres());
        u.setApellidos(dto.getApellidos());
        u.setTelefono(dto.getTelefono());
        usuarioService.update(u);
        return ResponseEntity.ok().build();
    }

    // DELETE /usuarios/miperfil (HU11)
    @DeleteMapping("/miperfil")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<Void> eliminarMiCuenta() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioService.findByUsername(username);
        usuarioService.delete(u.getId());
        return ResponseEntity.ok().build();
    }

    // GET /usuarios/miperfil/canales (HU06)
    @GetMapping("/miperfil/canales")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ASESOR')")
    public ResponseEntity<List<UsuarioCanalDTO>> misCanales() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioService.findByUsername(username);
        List<UsuarioCanalDTO> dtos = usuarioCanalService.listByUsuario(u.getId())
                .stream().map(uc -> {
                    UsuarioCanalDTO d = new UsuarioCanalDTO();
                    d.setId(uc.getId());
                    d.setCanalNombre(uc.getCanal().getNombre());
                    d.setIdentificadorExterno(uc.getIdentificadorExterno());
                    d.setNombreExterno(uc.getNombreExterno());
                    d.setActivo(uc.getActivo());
                    d.setFechaVinculacion(uc.getFechaVinculacion());
                    return d;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST /usuarios/miperfil/canales/vincular (HU06)
    @PostMapping("/miperfil/canales/vincular")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ASESOR')")
    public ResponseEntity<Void> vincularCanal(@RequestBody UsuarioCanalDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioService.findByUsername(username);
        usuarioCanalService.vincular(u.getId(), dto.getCanalNombre(),
                dto.getIdentificadorExterno(), dto.getNombreExterno());
        return ResponseEntity.ok().build();
    }

    // ── ADMIN ────────────────────────────────────────────────────

    // GET /usuarios (HU24)
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public List<UsuarioAdminDTO> listar() {
        return usuarioService.list().stream().map(u -> {
            UsuarioAdminDTO d = new UsuarioAdminDTO();
            d.setId(u.getId());
            d.setNombres(u.getNombres());
            d.setApellidos(u.getApellidos());
            d.setCorreo(u.getCorreo());
            d.setTelefono(u.getTelefono());
            d.setUsername(u.getUsername());
            d.setEstado(u.getEstado());
            d.setFechaRegistro(u.getFechaRegistro());
            d.setRoles(u.getRoles().stream().map(Rol::getRol).collect(Collectors.toList()));
            return d;
        }).collect(Collectors.toList());
    }

    // PUT /usuarios/{id}/estado (HU24)
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id) {
        usuarioService.cambiarEstado(id);
        return ResponseEntity.ok().build();
    }

    // PUT /usuarios/{id}/rol (HU24)
    @PutMapping("/{id}/rol")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> gestionarRol(@PathVariable Long id,
                                             @RequestBody AsignarRolDTO dto) {
        if ("ASIGNAR".equalsIgnoreCase(dto.getAccion())) {
            rolService.asignarRol(id, dto.getRol());
        } else {
            rolService.quitarRol(id, dto.getRol());
        }
        return ResponseEntity.ok().build();
    }
    // GET /usuarios/stats (conteo para panel ADMIN/ASESOR)
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMINISTRADOR') or hasAuthority('ASESOR')")
    public ResponseEntity<UsuarioStatsDTO> stats() {
        List<Usuario> todos = usuarioService.list();
        long total = todos.size();
        long activos = todos.stream().filter(u -> Boolean.TRUE.equals(u.getEstado())).count();

        UsuarioStatsDTO dto = new UsuarioStatsDTO();
        dto.setTotal(total);
        dto.setActivos(activos);
        dto.setInactivos(total - activos);

        // conteo por rol usando el query nativo que ya tienes
        java.util.Map<String, Long> porRol = new java.util.HashMap<>();
        for (Object[] fila : usuarioService.countByRol()) {
            porRol.put((String) fila[0], ((Number) fila[1]).longValue());
        }
        dto.setPorRol(porRol);
        return ResponseEntity.ok(dto);
    }

}