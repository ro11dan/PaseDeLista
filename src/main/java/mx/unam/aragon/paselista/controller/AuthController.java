package mx.unam.aragon.paselista.controller;

import mx.unam.aragon.paselista.model.Profesor;
import mx.unam.aragon.paselista.repository.ProfesorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final ProfesorRepository profesorRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(ProfesorRepository profesorRepo, PasswordEncoder passwordEncoder) {
        this.profesorRepo = profesorRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/materias";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarProfesor(@RequestParam String nombreCompleto,
                                    @RequestParam String correo,
                                    @RequestParam String password,
                                    Model model) {
        if (profesorRepo.existsByCorreo(correo.trim())) {
            model.addAttribute("error", "El correo ya se encuentra registrado.");
            return "registro";
        }
        Profesor p = new Profesor();
        p.setNombreCompleto(nombreCompleto.trim());
        p.setCorreo(correo.trim());
        p.setPassword(passwordEncoder.encode(password));
        profesorRepo.save(p);
        return "redirect:/login?registrado";
    }
}
