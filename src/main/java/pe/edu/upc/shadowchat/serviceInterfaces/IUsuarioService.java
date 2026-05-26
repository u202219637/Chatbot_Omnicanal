package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Usuario;

import java.util.List;

public interface IUsuarioService {

    // CRUD base — igual patrón AutoGuard
    List<Usuario> list();
    void insert(Usuario usuario);
    Usuario searchId(Long id);
    void update(Usuario usuario);
    void delete(Long id);

    // Seguridad / autenticación
    Usuario findByUsername(String username);
    Usuario findByCorreo(String correo);

    // Estado activo/inactivo (HU11, HU24)
    void cambiarEstado(Long id);

    // Estadística para dashboard (HU25)
    List<Object[]> countByRol();
}