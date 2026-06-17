package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Feedback;

import java.util.List;

public interface IFeedbackService {

    // Registrar calificación al cerrar conversación (HU23)
    void insert(Feedback feedback);

    // Listar feedbacks de una conversación
    List<Feedback> listByConversacion(Long conversacionId);

    // Distribución de calificaciones 1-5 para dashboard (HU25)
    List<Object[]> distribucionCalificaciones();

    // Motivos más frecuentes para reporte (HU26)
    List<Object[]> topMotivos();
    Object[] resumenPorAsesor(Long asesorId);
    List<Object[]> distribucionPorAsesor(Long asesorId);
    List<Object[]> comentariosRecientesPorAsesor(Long asesorId);
    List<Object[]> comentariosRecientesGlobal();
    List<Object[]> palabrasFrecuentes();
}