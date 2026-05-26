package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Categoria;
import pe.edu.upc.shadowchat.repositories.CategoriaRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.ICategoriaService;

import java.util.List;

@Service
public class CategoriaServiceImplement implements ICategoriaService {

    @Autowired private CategoriaRepository categoriaRepository;

    @Override public List<Categoria> list()           { return categoriaRepository.findAll(); }
    @Override public List<Categoria> listActivas()    { return categoriaRepository.findByEstadoTrue(); }
    @Override public void insert(Categoria c)         { categoriaRepository.save(c); }
    @Override public Categoria searchId(Long id)      { return categoriaRepository.findById(id).orElse(new Categoria()); }
    @Override public void update(Categoria c)         { categoriaRepository.save(c); }
    @Override public void delete(Long id)             { categoriaRepository.deleteById(id); }
}