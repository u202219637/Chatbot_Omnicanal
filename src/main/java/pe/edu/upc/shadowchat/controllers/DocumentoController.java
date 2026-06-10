package pe.edu.upc.shadowchat.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upc.shadowchat.dtos.documento.*;
import pe.edu.upc.shadowchat.entities.DocumentoConocimiento;
import pe.edu.upc.shadowchat.entities.FragmentoConocimiento;
import pe.edu.upc.shadowchat.entities.Producto;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.serviceInterfaces.IDocumentoConocimientoService;
import pe.edu.upc.shadowchat.serviceInterfaces.IFragmentoConocimientoService;
import pe.edu.upc.shadowchat.serviceInterfaces.IProductoService;
import pe.edu.upc.shadowchat.serviceInterfaces.IUsuarioService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/documentos")
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
@SecurityRequirement(name = "bearerAuth")
public class DocumentoController {

    @Autowired private IDocumentoConocimientoService documentoService;
    @Autowired private IFragmentoConocimientoService fragmentoService;
    @Autowired private IUsuarioService usuarioService;
    @Autowired private IProductoService productoService;

    // GET /admin/documentos
    @GetMapping
    public List<DocumentoListDTO> listar() {
        return documentoService.list().stream().map(doc -> {
            DocumentoListDTO d = new DocumentoListDTO();
            d.setId(doc.getId());
            d.setTitulo(doc.getTitulo());
            d.setTipoDocumento(doc.getTipoDocumento());
            d.setEstado(doc.getEstado());
            d.setFechaCarga(doc.getFechaCarga());
            d.setFechaProcesamiento(doc.getFechaProcesamiento());
            d.setCantidadFragmentos(fragmentoService.countByDocumento(doc.getId()));
            d.setUrlBlob(doc.getUrlBlob());
            d.setProductoNombre(doc.getProducto() != null ? doc.getProducto().getNombre() : null);
            return d;
        }).collect(Collectors.toList());
    }

    // GET /admin/documentos/estadisticas
    @GetMapping("/estadisticas")
    public ResponseEntity<RagEstadisticasDTO> estadisticas() {
        long totalDocs = documentoService.countDocumentos();
        long procesados = documentoService.listByEstado("PROCESADO").size();
        RagEstadisticasDTO d = new RagEstadisticasDTO();
        d.setDocumentosCargados(totalDocs);
        d.setBloquesInformacion(documentoService.countFragmentos());
        d.setTokensTotalesProcesados(documentoService.sumTokens());
        d.setPorcentajeCompletado(totalDocs > 0 ? (procesados * 100.0 / totalDocs) : 0.0);
        return ResponseEntity.ok(d);
    }

    // POST /admin/documentos/upload
    @PostMapping("/upload")
    public ResponseEntity<Void> upload(
            @RequestPart("archivo") MultipartFile archivo,
            @RequestPart("datos") DocumentoUploadDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario admin = usuarioService.findByUsername(username);
        documentoService.cargar(archivo, admin.getId(),
                dto.getProductoId(), dto.getTipoDocumento());
        return ResponseEntity.ok().build();
    }

    // GET /admin/documentos/{id}/fragmentos
    @GetMapping("/{id}/fragmentos")
    public List<FragmentoPreviewDTO> fragmentos(@PathVariable Long id) {
        return fragmentoService.listByDocumento(id).stream().map(f -> {
            FragmentoPreviewDTO d = new FragmentoPreviewDTO();
            d.setId(f.getId());
            d.setOrdenFragmento(f.getOrdenFragmento());
            d.setCantidadTokens(f.getCantidadTokens());
            String c = f.getContenido();
            d.setContenidoExtracto(c != null ? c.substring(0, Math.min(300, c.length())) : null);
            d.setEstado(f.getEstado());
            return d;
        }).collect(Collectors.toList());
    }

    // POST /admin/documentos/{id}/reprocesar
    @PostMapping("/{id}/reprocesar")
    public ResponseEntity<Void> reprocesar(@PathVariable Long id) {
        documentoService.reprocesar(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /admin/documentos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        documentoService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    // POST /admin/documentos/reindexar
    // Genera embeddings para todos los productos → RAG semántico real
    @PostMapping("/reindexar")
    public ResponseEntity<String> reindexar() {
        List<Producto> productos = productoService.list();
        int ok = 0, error = 0;
        for (Producto p : productos) {
            try {
                productoService.update(p);
                ok++;
            } catch (Exception e) {
                System.out.println("Error embedding " + p.getNombre() + ": " + e.getMessage());
                error++;
            }
        }
        return ResponseEntity.ok("Reindexados: " + ok + " | Errores: " + error);
    }
}