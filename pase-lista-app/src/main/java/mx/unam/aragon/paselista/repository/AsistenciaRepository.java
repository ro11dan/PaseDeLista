package mx.unam.aragon.paselista.repository;

import mx.unam.aragon.paselista.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    Optional<Asistencia> findByInscripcionIdInscripcionAndFechaSesion(Long idInscripcion, LocalDate fechaSesion);
    List<Asistencia> findByInscripcionMateriaIdMateriaAndFechaSesion(Long idMateria, LocalDate fechaSesion);
    List<Asistencia> findByInscripcionIdInscripcion(Long idInscripcion);
}
