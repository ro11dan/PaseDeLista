package mx.unam.aragon.paselista.service;

import mx.unam.aragon.paselista.model.Profesor;
import mx.unam.aragon.paselista.repository.ProfesorRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class ProfesorDetailsService implements UserDetailsService {

    private final ProfesorRepository profesorRepo;

    public ProfesorDetailsService(ProfesorRepository profesorRepo) {
        this.profesorRepo = profesorRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Profesor prof = profesorRepo.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Profesor no encontrado: " + correo));
        return new User(prof.getCorreo(), prof.getPassword(), Collections.emptyList());
    }
}
