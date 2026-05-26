package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Conversacion;
import pe.edu.upc.shadowchat.entities.Feedback;
import pe.edu.upc.shadowchat.repositories.ConversacionRepository;
import pe.edu.upc.shadowchat.repositories.FeedbackRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.IFeedbackService;

import java.util.List;

@Service
public class FeedbackServiceImplement implements IFeedbackService {

    @Autowired private FeedbackRepository feedbackRepository;
    @Autowired private ConversacionRepository conversacionRepository;

    @Override
    public void insert(Feedback feedback) {
        feedbackRepository.save(feedback);
        // Sincroniza satisfaccion en la conversacion para KPIs (HU25)
        Conversacion conv = feedback.getConversacion();
        if (conv != null && feedback.getCalificacion() != null) {
            conv.setSatisfaccion(feedback.getCalificacion());
            conversacionRepository.save(conv);
        }
    }

    @Override
    public List<Feedback> listByConversacion(Long conversacionId) {
        return feedbackRepository.findByConversacionId(conversacionId);
    }

    @Override
    public List<Object[]> distribucionCalificaciones() {
        return feedbackRepository.distribucionCalificaciones();
    }

    @Override
    public List<Object[]> topMotivos() {
        return feedbackRepository.topMotivos();
    }
}