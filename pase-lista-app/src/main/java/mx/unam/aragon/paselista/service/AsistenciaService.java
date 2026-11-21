package mx.unam.aragon.paselista.service;

import mx.unam.aragon.paselista.model.*;
import mx.unam.aragon.paselista.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepo;
    private final HorarioMateriaRepository horarioRepo;
    private final InscripcionRepository inscripcionRepo;

    public AsistenciaService(AsistenciaRepository asistenciaRepo,
                             HorarioMateriaRepository horarioRepo,
                             InscripcionRepository inscripcionRepo) {
        this.asistenciaRepo = asistenciaRepo;
        this.horarioRepo = horarioRepo;
        this.inscripcionRepo = inscripcionRepo;
    }

    /** Actor Reloj: valida periodo escolar (RN-05) y ventana horaria (RN-06) */
    public boolean esPaseListaHabilitado(Materia materia) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        if (hoy.isBefore(materia.getFechaInicio()) || hoy.isAfter(materia.getFechaFin())) {
            return false;
        }

        int diaHoy = hoy.getDayOfWeek().getValue();
        List<HorarioMateria> horarios = horarioRepo.findByMateriaIdMateria(materia.getIdMateria());

        return horarios.stream().anyMatch(h ->
                h.getDiaSemana() == diaHoy &&
                !ahora.isBefore(h.getHoraInicio()) &&
                !ahora.isAfter(h.getHoraFin())
        );
    }

    public Map<Long, String> obtenerEstadosDelDia(Long idMateria, LocalDate fecha) {
        Map<Long, String> mapa = new HashMap<>();
        for (Asistencia a : asistenciaRepo.findByInscripcionMateriaIdMateriaAndFechaSesion(idMateria, fecha)) {
            mapa.put(a.getInscripcion().getIdInscripcion(), a.getEstado());
        }
        return mapa;
    }

    /** Guarda o corrige (RF-14) la asistencia del día sin duplicar registros (RN-07) */
    @Transactional
    public void guardarPaseLista(Materia materia, Map<Long, String> estadosPorInscripcion) {
        if (!esPaseListaHabilitado(materia)) {
            throw new IllegalStateException("Fuera del día u horario autorizado para pasar lista.");
        }

        LocalDate hoy = LocalDate.now();
        for (Map.Entry<Long, String> entry : estadosPorInscripcion.entrySet()) {
            Long idInscripcion = entry.getKey();
            String estado = "Presente".equals(entry.getValue()) ? "Presente" : "Ausente";

            Inscripcion ins = inscripcionRepo.findById(idInscripcion).orElseThrow();
            Asistencia asistencia = asistenciaRepo
                    .findByInscripcionIdInscripcionAndFechaSesion(idInscripcion, hoy)
                    .orElseGet(() -> {
                        Asistencia nueva = new Asistencia();
                        nueva.setInscripcion(ins);
                        nueva.setFechaSesion(hoy);
                        return nueva;
                    });

            asistencia.setEstado(estado);
            asistenciaRepo.save(asistencia);
        }
    }

    public record ResumenAlumno(String numeroCuenta, String nombreCompleto,
                                long presentes, long faltas, long total, double porcentaje) {}

    public List<ResumenAlumno> generarReporte(Long idMateria) {
        List<Inscripcion> inscritos = inscripcionRepo.findByMateriaIdMateriaOrderByAlumnoNombreCompletoAsc(idMateria);
        List<ResumenAlumno> reporte = new ArrayList<>();

        for (Inscripcion ins : inscritos) {
            List<Asistencia> registros = asistenciaRepo.findByInscripcionIdInscripcion(ins.getIdInscripcion());
            long total = registros.size();
            long presentes = registros.stream().filter(a -> "Presente".equals(a.getEstado())).count();
            long faltas = total - presentes;
            double porcentaje = (total == 0) ? 0.0 : Math.round((presentes * 10000.0) / total) / 100.0;

            reporte.add(new ResumenAlumno(
                    ins.getAlumno().getNumeroCuenta(),
                    ins.getAlumno().getNombreCompleto(),
                    presentes, faltas, total, porcentaje
            ));
        }
        return reporte;
    }
}
