package pe.edu.upc.shadowchat.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.dtos.producto.*;
import pe.edu.upc.shadowchat.entities.Categoria;
import pe.edu.upc.shadowchat.entities.Marca;
import pe.edu.upc.shadowchat.entities.Producto;
import pe.edu.upc.shadowchat.serviceInterfaces.ICategoriaService;
import pe.edu.upc.shadowchat.serviceInterfaces.IMarcaService;
import pe.edu.upc.shadowchat.serviceInterfaces.IProductoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    @Autowired private IProductoService productoService;
    @Autowired private ICategoriaService categoriaService;
    @Autowired private IMarcaService marcaService;

    // GET /productos?categoriaId=&marcaId=&precioMin=&precioMax=&q= (HU07, HU08, HU09)
    @GetMapping
    public List<ProductoListDTO> catalogo(
            @RequestParam(required=false) Long categoriaId,
            @RequestParam(required=false) Long marcaId,
            @RequestParam(required=false) BigDecimal precioMin,
            @RequestParam(required=false) BigDecimal precioMax,
            @RequestParam(required=false) String q) {

        List<Producto> lista = (q != null && !q.isBlank())
                ? productoService.buscar(q)
                : productoService.filtrar(categoriaId, marcaId, precioMin, precioMax);

        return lista.stream().map(p -> {
            ProductoListDTO d = new ProductoListDTO();
            d.setId(p.getId());
            d.setNombre(p.getNombre());
            d.setCategoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null);
            d.setMarcaNombre(p.getMarca() != null ? p.getMarca().getNombre() : null);
            d.setEspecResumen(p.getDescripcion() != null
                    ? p.getDescripcion().substring(0, Math.min(60, p.getDescripcion().length()))
                    : null);
            d.setPrecio(p.getPrecio());
            d.setStock(p.getStock());
            d.setUrlImagen(p.getUrlImagen());
            d.setEstado(p.getEstado());
            return d;
        }).collect(Collectors.toList());
    }

    // GET /productos/{id} (HU10)
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDetalleDTO> detalle(@PathVariable Long id) {
        Producto p = productoService.searchId(id);
        ProductoDetalleDTO d = new ProductoDetalleDTO();
        d.setId(p.getId());
        d.setCodigoProducto(p.getCodigoProducto());
        d.setNombre(p.getNombre());
        d.setCategoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null);
        d.setMarcaNombre(p.getMarca() != null ? p.getMarca().getNombre() : null);
        d.setDescripcion(p.getDescripcion());
        d.setEspecificaciones(p.getEspecificaciones());
        d.setPrecio(p.getPrecio());
        d.setStock(p.getStock());
        d.setUrlImagen(p.getUrlImagen());
        d.setEstado(p.getEstado());
        d.setFechaActualizacion(p.getFechaActualizacion());
        // recomendacionIa se completa cuando se integre RagService/OpenAiService
        return ResponseEntity.ok(d);
    }

    // POST /productos/comparar (HU12)
    @PostMapping("/comparar")
    public List<ProductoDetalleDTO> comparar(@RequestBody List<Long> ids) {
        return productoService.comparar(ids).stream().map(p -> {
            ProductoDetalleDTO d = new ProductoDetalleDTO();
            d.setId(p.getId()); d.setNombre(p.getNombre());
            d.setDescripcion(p.getDescripcion());
            d.setEspecificaciones(p.getEspecificaciones());
            d.setPrecio(p.getPrecio()); d.setStock(p.getStock());
            return d;
        }).collect(Collectors.toList());
    }

    // ── ADMIN ────────────────────────────────────────────────────

    // POST /productos (HU32)
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> crear(@RequestBody ProductoFormDTO dto) {
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setCodigoProducto(dto.getCodigoProducto());
        p.setDescripcion(dto.getDescripcion());
        p.setEspecificaciones(dto.getEspecificaciones());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock() != null ? dto.getStock() : 0);
        p.setUrlImagen(dto.getUrlImagen());
        p.setEstado(true);
        if (dto.getCategoriaId() != null)
            p.setCategoria(categoriaService.searchId(dto.getCategoriaId()));
        if (dto.getMarcaId() != null)
            p.setMarca(marcaService.searchId(dto.getMarcaId()));
        productoService.insert(p);
        return ResponseEntity.ok().build();
    }

    // PUT /productos/{id} (HU32)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> editar(@PathVariable Long id,
                                       @RequestBody ProductoFormDTO dto) {
        Producto p = productoService.searchId(id);
        p.setNombre(dto.getNombre());
        p.setCodigoProducto(dto.getCodigoProducto());
        p.setDescripcion(dto.getDescripcion());
        p.setEspecificaciones(dto.getEspecificaciones());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        p.setUrlImagen(dto.getUrlImagen());
        p.setFechaActualizacion(LocalDateTime.now());
        if (dto.getCategoriaId() != null)
            p.setCategoria(categoriaService.searchId(dto.getCategoriaId()));
        if (dto.getMarcaId() != null)
            p.setMarca(marcaService.searchId(dto.getMarcaId()));
        productoService.update(p);
        return ResponseEntity.ok().build();
    }

    // PUT /productos/{id}/estado (HU32)
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id) {
        productoService.cambiarEstado(id);
        return ResponseEntity.ok().build();
    }
}