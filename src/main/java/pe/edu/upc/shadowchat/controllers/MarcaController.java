package pe.edu.upc.shadowchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.shadowchat.entities.Marca;
import pe.edu.upc.shadowchat.serviceInterfaces.IMarcaService;
import java.util.List;

@RestController
@RequestMapping("/marcas")
public class MarcaController {

    @Autowired
    private IMarcaService marcaService;

    @GetMapping
    public List<Marca> listar() {
        return marcaService.list();
    }
} 