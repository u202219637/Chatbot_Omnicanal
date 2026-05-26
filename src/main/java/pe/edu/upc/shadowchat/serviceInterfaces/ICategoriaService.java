package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Categoria;

import java.util.List;

public interface ICategoriaService {

    List<Categoria> list();
    List<Categoria> listActivas();
    void insert(Categoria categoria);
    Categoria searchId(Long id);
    void update(Categoria categoria);
    void delete(Long id);
}