package mx.unam.aragon.paselista.controller;

import mx.unam.aragon.paselista.model.HorarioMateria;
import mx.unam.aragon.paselista.model.Materia;
import mx.unam.aragon.paselista.model.Profesor;
import mx.unam.aragon.paselista.repository.ProfesorRepository;
import mx.unam.aragon.paselista.service.MateriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/materias")
public class MateriaController {

    private final MateriaService materiaService;
    private final ProfesorRepository profesorRepo;

    public MateriaController(MateriaService materiaService, ProfesorRepository profesorRepo) {
        this.materiaService = materiaService;
        this.profesorRepo = profesorRepo;
    }

    private Profesor getProfesorActual(Principal principal) {
        return profesorRepo.findByCorreo(principal.getName()).orElseThrow();
    }

    @GetMapping
    public String panelPrincipal(Principal principal, Model model) {
        Profesor prof = getProfesorActual(principal);
        model.addAttribute("profesor", prof);
        model.addAttribute("materias", materiaService.listarPorProfesor(prof));
        return "materias";
    }

    @GetMapping("/nueva")
    public String nuevaMateriaForm(Model model) {
        model.addAttribute("materia", new Materia());
        model.addAttribute("diasSeleccionados", Collections.emptyList());
        model.addAttribute("horaInicio", null);
        model.addAttribute("horaFin", null);
        return "materia-form";
    }

    @PostMapping("/guardar")
    public String guardarMateria(@RequestParam(required = false) Long idMateria,
                                 @RequestParam String nombreMateria,
                                 @RequestParam String claveMateria,
                                 @RequestParam String grupo,
                                 @RequestParam LocalDate fechaInicio,
                                 @RequestParam LocalDate fechaFin,
                                 @RequestParam(required = false) List<Integer> dias,
                                 @RequestParam LocalTime horaInicio,
                                 @RequestParam LocalTime horaFin,
                                 Principal principal, Model model) {
        Profesor prof = getProfesorActual(principal);
        Materia m = (idMateria != null) ? materiaService.obtenerDeProfesor(idMateria, prof) : new Materia();
        m.setNombreMateria(nombreMateria);
        m.setClaveMateria(claveMateria);
        m.setGrupo(grupo);
        m.setFechaInicio(fechaInicio);
        m.setFechaFin(fechaFin);

        try {
            materiaService.guardarMateria(m, prof, dias, horaInicio, horaFin);
            return "redirect:/materias";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("materia", m);
            model.addAttribute("diasSeleccionados", dias != null ? dias : Collections.emptyList());
            model.addAttribute("horaInicio", horaInicio);
            model.addAttribute("horaFin", horaFin);
            return "materia-form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarMateria(@PathVariable Long id, Principal principal, Model model) {
        Materia m = materiaService.obtenerDeProfesor(id, getProfesorActual(principal));
        List<Integer> dias = m.getHorarios().stream()
                .map(HorarioMateria::getDiaSemana)
                .collect(Collectors.toList());
        LocalTime horaInicio = m.getHorarios().isEmpty() ? null : m.getHorarios().get(0).getHoraInicio();
        LocalTime horaFin = m.getHorarios().isEmpty() ? null : m.getHorarios().get(0).getHoraFin();

        model.addAttribute("materia", m);
        model.addAttribute("diasSeleccionados", dias);
        model.addAttribute("horaInicio", horaInicio);
        model.addAttribute("horaFin", horaFin);
        return "materia-form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarMateria(@PathVariable Long id, Principal principal) {
        materiaService.eliminarMateria(id, getProfesorActual(principal));
        return "redirect:/materias";
    }
}
