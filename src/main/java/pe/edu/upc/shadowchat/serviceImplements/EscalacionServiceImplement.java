package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Conversacion;
import pe.edu.upc.shadowchat.entities.Escalacion;
import pe.edu.upc.shadowchat.entities.Usuario;
import pe.edu.upc.shadowchat.repositories.ConversacionRepository;
import pe.edu.upc.shadowchat.repositories.EscalacionRepository;
import pe.edu.upc.shadowchat.repositories.UsuarioRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IEscalacionService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EscalacionServiceImplement implements IEscalacionService {

    @Autowired private EscalacionRepository escalacionRepository;
    @Autowired private ConversacionRepository conversacionRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Override
    public Escalacion crear(Long conversacionId, String motivo, String prioridad) {
        Conversacion conv = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new RuntimeException("Conversacion no encontrada: " + conversacionId));

        Escalacion e = new Escalacion();
        e.setConversacion(conv);
        e.setMotivo(motivo);
        e.setPrioridad(prioridad != null ? prioridad : "MEDIA");
        e.setEstado("PENDIENTE");
        escalacionRepository.save(e);

        // Marca la conversacion como escalada
        conv.setEstado("ESCALADA");
        conv.setFueEscalada(true);
        conversacionRepository.save(conv);

        return e;
    }

    @Override
    public List<Escalacion> listActivas() {
        return escalacionRepository.findByEstadoInOrderByPrioridadAscFechaCreacionAsc(
                List.of("PENDIENTE", "ASIGNADA", "EN_ATENCION"));
    }

    @Override
    public List<Escalacion> listByAsesor(Long asesorId, String estado) {
        return escalacionRepository.findByAsesorIdAndEstadoOrderByFechaCreacionAsc(asesorId, estado);
    }

    @Override
    public void asignar(Long escalacionId, Long asesorId) {
        Escalacion e = escalacionRepository.findById(escalacionId)
                .orElseThrow(() -> new RuntimeException("Escalacion no encontrada: " + escalacionId));
        Usuario asesor = usuarioRepository.findById(asesorId)
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado: " + asesorId));
        e.setAsesor(asesor);
        e.setEstado("ASIGNADA");
        e.setFechaAsignacion(LocalDateTime.now());
        escalacionRepository.save(e);
    }

    @Override
    public void iniciarAtencion(Long escalacionId) {
        escalacionRepository.findById(escalacionId).ifPresent(e -> {
            e.setEstado("EN_ATENCION");
            escalacionRepository.save(e);
        });
    }

    @Override
    public void cerrar(Long escalacionId) {
        escalacionRepository.findById(escalacionId).ifPresent(e -> {
            e.setEstado("CERRADA");
            e.setFechaCierre(LocalDateTime.now());
            escalacionRepository.save(e);
            // Cierra también la conversacion asociada
            Conversacion conv = e.getConversacion();
            conv.setEstado("CERRADA");
            conversacionRepository.save(conv);
        });
    }

    @Override
    public List<Escalacion> listByConversacion(Long conversacionId) {
        return escalacionRepository.findByConversacionId(conversacionId);
    }

    @Override
    public long countByEstado(String estado) {
        return escalacionRepository.countByEstado(estado);
    }
}