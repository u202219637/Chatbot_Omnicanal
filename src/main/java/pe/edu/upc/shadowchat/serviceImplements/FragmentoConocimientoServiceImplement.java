package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.FragmentoConocimiento;
import pe.edu.upc.shadowchat.repositories.FragmentoConocimientoRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IFragmentoConocimientoService;

import java.util.List;

@Service
public class FragmentoConocimientoServiceImplement implements IFragmentoConocimientoService {

    @Autowired private FragmentoConocimientoRepository fragmentoRepository;

    @Override
    public List<FragmentoConocimiento> listByDocumento(Long documentoId) {
        return fragmentoRepository
                .findByDocumentoConocimientoIdOrderByOrdenFragmentoAsc(documentoId);
    }

    @Override
    public long countByDocumento(Long documentoId) {
        return fragmentoRepository.countByDocumentoConocimientoId(documentoId);
    }

    @Override
    public List<Object[]> buscarSimilares(float[] embedding, int topK) {
        return fragmentoRepository.findTopKWithScore(embedding, topK);
    }

    @Override
    public void deleteByDocumento(Long documentoId) {
        fragmentoRepository.deleteByDocumentoConocimientoId(documentoId);
    }
}