package mx.unam.aragon.paselista.controller;

import mx.unam.aragon.paselista.model.Alumno;
import mx.unam.aragon.paselista.model.Materia;
import mx.unam.aragon.paselista.model.Profesor;
import mx.unam.aragon.paselista.repository.ProfesorRepository;
import mx.unam.aragon.paselista.service.AlumnoService;
import mx.unam.aragon.paselista.service.MateriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/materias/{idMateria}/alumnos")
public class AlumnoController {

    private final MateriaService materiaService;
    private final AlumnoService alumnoService;
    private final ProfesorRepository profesorRepo;

    public AlumnoController(MateriaService materiaService, AlumnoService alumnoService,
                            ProfesorRepository profesorRepo) {
        this.materiaService = materiaService;
        this.alumnoService = alumnoService;
        this.profesorRepo = profesorRepo;
    }

    private Materia getMateria(Long idMateria, Principal principal) {
        Profesor prof = profesorRepo.findByCorreo(principal.getName()).orElseThrow();
        return materiaService.obtenerDeProfesor(idMateria, prof);
    }

    @GetMapping
    public String gestionarAlumnos(@PathVariable Long idMateria, Principal principal, Model model) {
        Materia materia = getMateria(idMateria, principal);
        model.addAttribute("materia", materia);
        model.addAttribute("inscripciones", alumnoService.listarInscritos(idMateria));
        return "alumnos";
    }

    @PostMapping("/agregar")
    public String agregarAlumno(@PathVariable Long idMateria,
                                @RequestParam String numeroCuenta,
                                @RequestParam String nombreCompleto,
                                @RequestParam String correoElectronico,
                                Principal principal, RedirectAttributes ra) {
        try {
            Materia materia = getMateria(idMateria, principal);
            alumnoService.inscribirAlumno(materia, numeroCuenta, nombreCompleto, correoElectronico);
            ra.addFlashAttribute("mensaje", "Alumno inscrito correctamente.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/materias/" + idMateria + "/alumnos";
    }

    @GetMapping("/{idAlumno}/editar")
    public String editarAlumnoForm(@PathVariable Long idMateria, @PathVariable Long idAlumno,
                                   Principal principal, Model model) {
        Materia materia = getMateria(idMateria, principal);
        Alumno alumno = alumnoService.obtenerPorId(idAlumno);
        model.addAttribute("materia", materia);
        model.addAttribute("alumno", alumno);
        return "alumno-editar";
    }

    @PostMapping("/{idAlumno}/editar")
    public String actualizarAlumno(@PathVariable Long idMateria, @PathVariable Long idAlumno,
                                   @RequestParam String nombreCompleto,
                                   @RequestParam String correoElectronico,
                                   Principal principal, RedirectAttributes ra) {
        getMateria(idMateria, principal);
        try {
            alumnoService.actualizarAlumno(idAlumno, nombreCompleto, correoElectronico);
            ra.addFlashAttribute("mensaje", "Datos del alumno actualizados.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/materias/" + idMateria + "/alumnos";
    }

    @PostMapping("/cargar-csv")
    public String cargarCsv(@PathVariable Long idMateria,
                            @RequestParam("archivo") MultipartFile archivo,
                            Principal principal, RedirectAttributes ra) {
        try {
            Materia materia = getMateria(idMateria, principal);
            int total = alumnoService.cargarDesdeCsv(materia, archivo);
            ra.addFlashAttribute("mensaje", "Se inscribieron " + total + " alumnos desde el archivo.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al procesar el archivo CSV.");
        }
        return "redirect:/materias/" + idMateria + "/alumnos";
    }

    @PostMapping("/{idInscripcion}/quitar")
    public String quitarAlumno(@PathVariable Long idMateria, @PathVariable Long idInscripcion,
                               Principal principal, RedirectAttributes ra) {
        getMateria(idMateria, principal);
        alumnoService.quitarDeMateria(idInscripcion, idMateria);
        ra.addFlashAttribute("mensaje", "Alumno removido de la materia.");
        return "redirect:/materias/" + idMateria + "/alumnos";
    }
}
