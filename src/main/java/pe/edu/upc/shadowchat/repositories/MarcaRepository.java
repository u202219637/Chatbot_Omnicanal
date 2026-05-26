package pe.edu.upc.shadowchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.shadowchat.entities.Marca;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findByEstadoTrue();
}