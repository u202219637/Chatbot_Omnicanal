package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.FuenteRespuesta;
import pe.edu.upc.shadowchat.repositories.FuenteRespuestaRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IFuenteRespuestaService;

import java.util.List;

@Service
public class FuenteRespuestaServiceImplement implements IFuenteRespuestaService {

    @Autowired private FuenteRespuestaRepository fuenteRespuestaRepository;

    @Override
    public void insert(FuenteRespuesta fr) {
        fuenteRespuestaRepository.save(fr);
    }

    @Override
    public List<FuenteRespuesta> listByMensaje(Long mensajeId) {
        return fuenteRespuestaRepository.findByMensajeIdOrderByScoreRelevanciaDesc(mensajeId);
    }
}