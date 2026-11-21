package mx.unam.aragon.paselista.service;

import mx.unam.aragon.paselista.model.*;
import mx.unam.aragon.paselista.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.List;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepo;

    public MateriaService(MateriaRepository materiaRepo) {
        this.materiaRepo = materiaRepo;
    }

    public List<Materia> listarPorProfesor(Profesor profesor) {
        return materiaRepo.findByProfesorIdProfesor(profesor.getIdProfesor());
    }

    public Materia obtenerDeProfesor(Long idMateria, Profesor profesor) {
        return materiaRepo.findByIdMateriaAndProfesorIdProfesor(idMateria, profesor.getIdProfesor())
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada o acceso denegado."));
    }

    @Transactional
    public Materia guardarMateria(Materia materia, Profesor profesor, List<Integer> dias,
                                  LocalTime horaInicio, LocalTime horaFin) {
        if (materia.getFechaInicio() == null || materia.getFechaFin() == null ||
                !materia.getFechaInicio().isBefore(materia.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        if (horaInicio == null || horaFin == null || !horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        if (dias == null || dias.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un día de clase.");
        }

        materia.setProfesor(profesor);
        materia.getHorarios().clear();

        for (Integer dia : dias) {
            HorarioMateria h = new HorarioMateria();
            h.setMateria(materia);
            h.setDiaSemana(dia);
            h.setHoraInicio(horaInicio);
            h.setHoraFin(horaFin);
            materia.getHorarios().add(h);
        }
        return materiaRepo.save(materia);
    }

    @Transactional
    public void eliminarMateria(Long idMateria, Profesor profesor) {
        Materia m = obtenerDeProfesor(idMateria, profesor);
        materiaRepo.delete(m);
    }
}
