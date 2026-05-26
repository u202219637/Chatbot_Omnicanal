package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.Escalacion;

import java.util.List;

@Repository
public interface EscalacionRepository extends JpaRepository<Escalacion, Long> {

    // Escalaciones activas — cola del asesor (HU22)
    // Estados: PENDIENTE, ASIGNADA, EN_ATENCION
    List<Escalacion> findByEstadoInOrderByPrioridadAscFechaCreacionAsc(
            List<String> estados);

    // Escalaciones asignadas a un asesor específico (HU22)
    List<Escalacion> findByAsesorIdAndEstadoOrderByFechaCreacionAsc(
            Long asesorId, String estado);

    // Escalaciones de una conversación (contexto del asesor)
    List<Escalacion> findByConversacionId(Long conversacionId);

    // Conteo por estado (HU25 — dashboard)
    long countByEstado(String estado);
}