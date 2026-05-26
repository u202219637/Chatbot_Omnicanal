package pe.edu.upc.shadowchat.serviceInterfaces;

import pe.edu.upc.shadowchat.entities.Canal;

import java.util.List;
import java.util.Optional;

public interface ICanalService {

    List<Canal> list();
    Canal searchId(Long id);
    Optional<Canal> findByNombre(String nombre);
}