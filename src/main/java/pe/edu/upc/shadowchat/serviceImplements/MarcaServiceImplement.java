package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Marca;
import pe.edu.upc.shadowchat.repositories.MarcaRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IMarcaService;

import java.util.List;

@Service
public class MarcaServiceImplement implements IMarcaService {

    @Autowired private MarcaRepository marcaRepository;

    @Override public List<Marca> list()               { return marcaRepository.findAll(); }
    @Override public List<Marca> listActivas()        { return marcaRepository.findByEstadoTrue(); }
    @Override public void insert(Marca m)             { marcaRepository.save(m); }
    @Override public Marca searchId(Long id)          { return marcaRepository.findById(id).orElse(new Marca()); }
    @Override public void update(Marca m)             { marcaRepository.save(m); }
    @Override public void delete(Long id)             { marcaRepository.deleteById(id); }
}