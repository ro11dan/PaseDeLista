package mx.unam.aragon.paselista.repository;

import mx.unam.aragon.paselista.model.HorarioMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioMateriaRepository extends JpaRepository<HorarioMateria, Long> {
    List<HorarioMateria> findByMateriaIdMateria(Long idMateria);
}
