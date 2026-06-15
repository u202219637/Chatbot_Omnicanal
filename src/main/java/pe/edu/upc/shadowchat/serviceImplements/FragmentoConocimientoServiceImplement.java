package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.FragmentoConocimiento;
import pe.edu.upc.shadowchat.repositories.FragmentoConocimientoRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IFragmentoConocimientoService;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<Object[]> buscarSimilares(float[] embRaw, int topK) {
        String embedding = "[" + java.util.stream.IntStream.range(0, embRaw.length)
                .mapToObj(i -> String.valueOf(embRaw[i]))
                .collect(Collectors.joining(",")) + "]";
        return fragmentoRepository.findTopKWithScore(embedding, topK);
    }

    @Override
    public void deleteByDocumento(Long documentoId) {
        fragmentoRepository.deleteByDocumentoConocimientoId(documentoId);
    }
}