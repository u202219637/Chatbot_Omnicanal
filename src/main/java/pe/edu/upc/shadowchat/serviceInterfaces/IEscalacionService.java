package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Escalacion;

import java.util.List;

public interface IEscalacionService {

    /*
     * Crea una escalación desde el bot cuando confianza_ia < 0.6 (HU22).
     * También marca la conversación como ESCALADA.
     */
    Escalacion crear(Long conversacionId, String motivo, String prioridad);

    // Cola de escalaciones activas para el asesor (HU22)
    List<Escalacion> listActivas();

    // Escalaciones asignadas a un asesor específico (HU22)
    List<Escalacion> listByAsesor(Long asesorId, String estado);

    // Asigna un asesor a la escalación — estado → ASIGNADA
    void asignar(Long escalacionId, Long asesorId);

    // El asesor toma la atención — estado → EN_ATENCION
    void iniciarAtencion(Long escalacionId);

    // El asesor cierra la escalación — estado → RESUELTA/CERRADA
    void cerrar(Long escalacionId);

    // Escalaciones de una conversación (contexto del asesor)
    List<Escalacion> listByConversacion(Long conversacionId);

    // Conteos para el resumen del dashboard (HU25)
    long countByEstado(String estado);
    void resolver(Long escalacionId);
    List<Escalacion> listTodas(String estado);
    void cambiarPrioridad(Long escalacionId, String prioridad);
    Escalacion findById(Long id);
}