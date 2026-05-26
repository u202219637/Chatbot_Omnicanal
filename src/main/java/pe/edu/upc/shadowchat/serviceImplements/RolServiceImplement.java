package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Rol;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.repositories.RolRepository;
import pe.edu.upc.shadowchat.repositories.UsuarioRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IRolService;

import java.util.List;

@Service
public class RolServiceImplement implements IRolService {

    @Autowired private RolRepository rolRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Override
    public void insert(Rol rol) {
        rolRepository.save(rol);
    }

    @Override
    public void delete(Long id) {
        rolRepository.deleteById(id);
    }

    @Override
    public List<Rol> listByUser(Long usuarioId) {
        return rolRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public void asignarRol(Long usuarioId, String rol) {
        if (!rolRepository.existsByUsuarioIdAndRol(usuarioId, rol)) {
            Usuario u = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));
            Rol nuevoRol = new Rol();
            nuevoRol.setRol(rol);
            nuevoRol.setUsuario(u);
            rolRepository.save(nuevoRol);
        }
    }

    @Override
    public void quitarRol(Long usuarioId, String rol) {
        rolRepository.deleteByUsuarioIdAndRol(usuarioId, rol);
    }
}