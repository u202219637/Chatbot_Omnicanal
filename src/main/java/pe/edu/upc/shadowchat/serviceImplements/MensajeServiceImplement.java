package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Mensaje;
import pe.edu.upc.shadowchat.repositories.MensajeRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IMensajeService;

import java.util.List;

@Service
public class MensajeServiceImplement implements IMensajeService {

    @Autowired private MensajeRepository mensajeRepository;

    @Override
    public void insert(Mensaje m) {
        mensajeRepository.save(m);
    }

    @Override
    public Mensaje searchId(Long id) {
        return mensajeRepository.findById(id).orElse(new Mensaje());
    }

    @Override
    public List<Mensaje> listByConversacion(Long conversacionId) {
        return mensajeRepository.findByConversacionIdOrderByFechaEnvioAsc(conversacionId);
    }

    @Override
    public List<Object[]> documentosMasUsados() {
        return mensajeRepository.documentosMasUsados();
    }

    @Override
    public Object[] sumaTokens(Long conversacionId) {
        return mensajeRepository.sumaTokensByConversacion(conversacionId);
    }
    @Override
    public long countByConversacionIdAndTipoEmisor(Long conversacionId, String tipoEmisor) {
        return mensajeRepository.countByConversacionIdAndTipoEmisor(conversacionId, tipoEmisor);
    }
}