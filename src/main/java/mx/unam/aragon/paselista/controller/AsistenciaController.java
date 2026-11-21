package mx.unam.aragon.paselista.controller;

import mx.unam.aragon.paselista.model.Materia;
import mx.unam.aragon.paselista.model.Profesor;
import mx.unam.aragon.paselista.repository.ProfesorRepository;
import mx.unam.aragon.paselista.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/materias/{idMateria}")
public class AsistenciaController {

    private final MateriaService materiaService;
    private final AlumnoService alumnoService;
    private final AsistenciaService asistenciaService;
    private final ProfesorRepository profesorRepo;

    public AsistenciaController(MateriaService materiaService, AlumnoService alumnoService,
                                AsistenciaService asistenciaService, ProfesorRepository profesorRepo) {
        this.materiaService = materiaService;
        this.alumnoService = alumnoService;
        this.asistenciaService = asistenciaService;
        this.profesorRepo = profesorRepo;
    }

    private Materia getMateria(Long idMateria, Principal principal) {
        Profesor prof = profesorRepo.findByCorreo(principal.getName()).orElseThrow();
        return materiaService.obtenerDeProfesor(idMateria, prof);
    }

    @GetMapping("/asistencia")
    public String verPaseLista(@PathVariable Long idMateria, Principal principal, Model model) {
        Materia materia = getMateria(idMateria, principal);
        boolean habilitado = asistenciaService.esPaseListaHabilitado(materia);

        model.addAttribute("materia", materia);
        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("horaActual", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        model.addAttribute("habilitado", habilitado);
        model.addAttribute("inscripciones", alumnoService.listarInscritos(idMateria));
        model.addAttribute("estadosHoy", asistenciaService.obtenerEstadosDelDia(idMateria, LocalDate.now()));
        return "asistencia";
    }

    @PostMapping("/asistencia/guardar")
    public String guardarPaseLista(@PathVariable Long idMateria,
                                   @RequestParam Map<String, String> allParams,
                                   Principal principal, RedirectAttributes ra) {
        Materia materia = getMateria(idMateria, principal);
        Map<Long, String> estados = new HashMap<>();

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("estado_")) {
                Long idInscripcion = Long.parseLong(entry.getKey().replace("estado_", ""));
                estados.put(idInscripcion, entry.getValue());
            }
        }

        try {
            asistenciaService.guardarPaseLista(materia, estados);
            ra.addFlashAttribute("mensaje", "Pase de lista guardado/actualizado correctamente.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/materias/" + idMateria + "/asistencia";
    }

    @GetMapping("/reporte")
    public String verReporte(@PathVariable Long idMateria, Principal principal, Model model) {
        Materia materia = getMateria(idMateria, principal);
        model.addAttribute("materia", materia);
        model.addAttribute("reporte", asistenciaService.generarReporte(idMateria));
        return "reporte";
    }
}
