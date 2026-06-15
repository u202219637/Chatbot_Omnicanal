package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Conversacion;
import pe.edu.upc.shadowchat.repositories.ConversacionRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IConversacionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversacionServiceImplement implements IConversacionService {

    @Autowired private ConversacionRepository conversacionRepository;

    @Override
    public void insert(Conversacion c) {
        conversacionRepository.save(c);
    }

    @Override
    public Conversacion searchId(Long id) {
        return conversacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversacion no encontrada: " + id));
    }

    @Override
    public void update(Conversacion c) {
        conversacionRepository.save(c);
    }

    @Override
    public List<Conversacion> listByUsuario(Long usuarioId) {
        return conversacionRepository.findByUsuarioIdOrderByFechaInicioDesc(usuarioId);
    }

    @Override
    public Optional<Conversacion> findActiva(Long usuarioId, String origen) {
        return conversacionRepository
                .findByUsuarioIdAndOrigenAndEstado(usuarioId, origen, "ABIERTA");
    }

    @Override
    public void cerrar(Long id, Boolean fueResuelta) {
        conversacionRepository.findById(id).ifPresent(c -> {
            c.setEstado("RESUELTA");
            c.setFueResuelta(Boolean.TRUE.equals(fueResuelta));
            c.setFechaFin(LocalDateTime.now());
            conversacionRepository.save(c);
        });
    }

    @Override
    public void marcarEscalada(Long id) {
        conversacionRepository.findById(id).ifPresent(c -> {
            c.setEstado("ESCALADA");
            c.setFueEscalada(true);
            conversacionRepository.save(c);
        });
    }

    @Override
    public Optional<Conversacion> findMasRecienteByUsuario(Long usuarioId) {
        return conversacionRepository
                .findFirstByUsuarioIdOrderByFechaInicioDesc(usuarioId);
    }
    @Override
    public List<Object[]> convsPorDia() {
        return conversacionRepository.convsPorDia();
    }

    @Override public Object[]       kpisMes()                          { return conversacionRepository.kpisMes(); }
    @Override public Object[]       kpisPeriodo(String d, String h)    { return conversacionRepository.kpisPeriodo(d, h); }
    @Override public List<Object[]> topIntenciones()                   { return conversacionRepository.topIntenciones(); }
    @Override public List<Object[]> tokensPorDia()                     { return conversacionRepository.tokensPorDia(); }
    @Override public List<Object[]> countByOrigen()                    { return conversacionRepository.countByOrigen(); }

    @Override
    public Optional<Conversacion> findActivaByUsuario(Long usuarioId) {
        return conversacionRepository
                .findFirstByUsuarioIdAndEstadoOrderByFechaInicioDesc(usuarioId, "ABIERTA");
    }

    @Override
    public List<Conversacion> listAll(String estado, String origen,
                                      LocalDateTime desde, LocalDateTime hasta) {
        return conversacionRepository.findAllWithFilters(estado, origen, desde, hasta);
    }
    @Override
    public Optional<Conversacion> findActivaOEscaladaByUsuario(Long usuarioId) {
        return conversacionRepository
                .findFirstByUsuarioIdAndEstadoInOrderByFechaInicioDesc(
                        usuarioId, List.of("ABIERTA", "ESCALADA"));
    }

}