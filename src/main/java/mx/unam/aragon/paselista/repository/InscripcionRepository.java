package mx.unam.aragon.paselista.repository;

import mx.unam.aragon.paselista.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByMateriaIdMateriaOrderByAlumnoNombreCompletoAsc(Long idMateria);
    Optional<Inscripcion> findByAlumnoIdAlumnoAndMateriaIdMateria(Long idAlumno, Long idMateria);
    boolean existsByAlumnoIdAlumnoAndMateriaIdMateria(Long idAlumno, Long idMateria);
}
