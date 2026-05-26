package pe.edu.upc.shadowchat.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.shadowchat.entities.Canal;
import pe.edu.upc.shadowchat.repositories.CanalRepository;
import pe.edu.upc.shadowchat.serviceInterfaces.ICanalService;

import java.util.List;
import java.util.Optional;

@Service
public class CanalServiceImplement implements ICanalService {

    @Autowired private CanalRepository canalRepository;

    @Override public List<Canal> list()               { return canalRepository.findAll(); }
    @Override public Canal searchId(Long id)          { return canalRepository.findById(id).orElse(new Canal()); }
    @Override public Optional<Canal> findByNombre(String nombre) { return canalRepository.findByNombre(nombre); }
}