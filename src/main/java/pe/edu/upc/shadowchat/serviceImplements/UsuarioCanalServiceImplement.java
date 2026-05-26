package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Canal;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.entities.UsuarioCanal;
import pe.edu.upc.shadowchat.repositories.CanalRepository;
import pe.edu.upc.shadowchat.repositories.UsuarioCanalRepository;
import pe.edu.upc.shadowchat.repositories.UsuarioRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IUsuarioCanalService;

import java.util.List;

@Service
public class UsuarioCanalServiceImplement implements IUsuarioCanalService {

    @Autowired private UsuarioCanalRepository usuarioCanalRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CanalRepository canalRepository;

    @Override
    public void insert(UsuarioCanal uc) {
        usuarioCanalRepository.save(uc);
    }

    @Override
    public void delete(Long id) {
        usuarioCanalRepository.deleteById(id);
    }

    @Override
    public List<UsuarioCanal> listByUsuario(Long usuarioId) {
        return usuarioCanalRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    @Override
    public UsuarioCanal findByIdentificador(String canalNombre, String identificadorExterno) {
        return usuarioCanalRepository
                .findByCanalNombreAndIdentificadorExterno(canalNombre, identificadorExterno)
                .orElseThrow(() -> new RuntimeException(
                        "Canal no vinculado: " + canalNombre + " / " + identificadorExterno));
    }

    @Override
    public UsuarioCanal vincular(Long usuarioId, String canalNombre,
                                 String identificadorExterno, String nombreExterno) {
        Canal canal = canalRepository.findByNombre(canalNombre)
                .orElseThrow(() -> new RuntimeException("Canal no existe: " + canalNombre));

        if (usuarioCanalRepository.existsByCanalIdAndIdentificadorExterno(
                canal.getId(), identificadorExterno)) {
            throw new RuntimeException("Identificador ya vinculado: " + identificadorExterno);
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        UsuarioCanal uc = new UsuarioCanal();
        uc.setUsuario(usuario);
        uc.setCanal(canal);
        uc.setIdentificadorExterno(identificadorExterno);
        uc.setNombreExterno(nombreExterno);
        uc.setActivo(true);
        return usuarioCanalRepository.save(uc);
    }

    @Override
    public void desactivar(Long id) {
        usuarioCanalRepository.findById(id).ifPresent(uc -> {
            uc.setActivo(false);
            usuarioCanalRepository.save(uc);
        });
    }
}