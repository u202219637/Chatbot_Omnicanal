package pe.edu.upc.shadowchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.entities.Categoria;
import pe.edu.upc.shadowchat.serviceInterfaces.ICategoriaService;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private ICategoriaService categoriaService;

    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.list();
    }
}