package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.entities.UsuarioCanal;

import java.util.List;

public interface IUsuarioCanalService {

    // CRUD base
    void insert(UsuarioCanal usuarioCanal);
    void delete(Long id);
    List<UsuarioCanal> listByUsuario(Long usuarioId);

    /*
     * CLAVE omnicanal (HU06, HU20):
     * Recibe el identificador externo (número WhatsApp o correo web)
     * y devuelve el UsuarioCanal que mapea a un Usuario del sistema.
     * Usado por TwilioWebhookController al recibir un mensaje entrante.
     */
    UsuarioCanal findByIdentificador(String canalNombre, String identificadorExterno);

    // Vincula un número WhatsApp / correo a un usuario (HU06)
    UsuarioCanal vincular(Long usuarioId, String canalNombre, String identificadorExterno,
                          String nombreExterno);

    // Desactiva un vínculo sin borrarlo (auditoría)
    void desactivar(Long id);
    Usuario findUsuarioByTelefono(String telefonoConPlus, String telefonoSinPlus);
    UsuarioCanal save(UsuarioCanal uc);
}