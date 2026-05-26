package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Marca;

import java.util.List;

public interface IMarcaService {

    List<Marca> list();
    List<Marca> listActivas();
    void insert(Marca marca);
    Marca searchId(Long id);
    void update(Marca marca);
    void delete(Long id);
}