package mx.unam.aragon.paselista.repository;

import mx.unam.aragon.paselista.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    List<Materia> findByProfesorIdProfesor(Long idProfesor);
    Optional<Materia> findByIdMateriaAndProfesorIdProfesor(Long idMateria, Long idProfesor);
}
