package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Rol;

import java.util.List;

public interface IRolService {

    // CRUD base
    void insert(Rol rol);
    void delete(Long id);
    List<Rol> listByUser(Long usuarioId);

    // Asignar rol a usuario sin duplicar (HU24)
    void asignarRol(Long usuarioId, String rol);

    // Quitar rol específico (HU24)
    void quitarRol(Long usuarioId, String rol);
}