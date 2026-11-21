package mx.unam.aragon.paselista.service;

import mx.unam.aragon.paselista.model.*;
import mx.unam.aragon.paselista.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class AlumnoService {

    private final AlumnoRepository alumnoRepo;
    private final InscripcionRepository inscripcionRepo;

    public AlumnoService(AlumnoRepository alumnoRepo, InscripcionRepository inscripcionRepo) {
        this.alumnoRepo = alumnoRepo;
        this.inscripcionRepo = inscripcionRepo;
    }

    public List<Inscripcion> listarInscritos(Long idMateria) {
        return inscripcionRepo.findByMateriaIdMateriaOrderByAlumnoNombreCompletoAsc(idMateria);
    }

    public Alumno obtenerPorId(Long idAlumno) {
        return alumnoRepo.findById(idAlumno)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado."));
    }

    @Transactional
    public void inscribirAlumno(Materia materia, String numeroCuenta, String nombreCompleto, String correo) {
        if (numeroCuenta == null || !numeroCuenta.trim().matches("\\d{9}")) {
            throw new IllegalArgumentException("El número de cuenta debe tener exactamente 9 dígitos.");
        }
        if (nombreCompleto == null || nombreCompleto.isBlank() || correo == null || !correo.contains("@")) {
            throw new IllegalArgumentException("Nombre completo y correo válido son obligatorios.");
        }

        String cuenta = numeroCuenta.trim();
        String nombre = nombreCompleto.trim();
        String email = correo.trim();

        Alumno alumno = alumnoRepo.findByNumeroCuenta(cuenta).orElseGet(() -> {
            if (alumnoRepo.existsByCorreoElectronico(email)) {
                throw new IllegalArgumentException("El correo ya está registrado con otro número de cuenta.");
            }
            Alumno nuevo = new Alumno();
            nuevo.setNumeroCuenta(cuenta);
            nuevo.setNombreCompleto(nombre);
            nuevo.setCorreoElectronico(email);
            return alumnoRepo.save(nuevo);
        });

        if (inscripcionRepo.existsByAlumnoIdAlumnoAndMateriaIdMateria(alumno.getIdAlumno(), materia.getIdMateria())) {
            throw new IllegalArgumentException("El alumno ya está inscrito en esta materia.");
        }

        Inscripcion ins = new Inscripcion();
        ins.setAlumno(alumno);
        ins.setMateria(materia);
        ins.setFechaInscripcion(LocalDate.now());
        inscripcionRepo.save(ins);
    }

    @Transactional
    public void actualizarAlumno(Long idAlumno, String nombreCompleto, String correoElectronico) {
        if (nombreCompleto == null || nombreCompleto.isBlank() || correoElectronico == null || !correoElectronico.contains("@")) {
            throw new IllegalArgumentException("Nombre completo y correo válido son obligatorios.");
        }
        Alumno alumno = obtenerPorId(idAlumno);
        String email = correoElectronico.trim();
        if (!alumno.getCorreoElectronico().equalsIgnoreCase(email) && alumnoRepo.existsByCorreoElectronico(email)) {
            throw new IllegalArgumentException("Ese correo ya pertenece a otro alumno.");
        }
        alumno.setNombreCompleto(nombreCompleto.trim());
        alumno.setCorreoElectronico(email);
        alumnoRepo.save(alumno);
    }

    @Transactional
    public int cargarDesdeCsv(Materia materia, MultipartFile archivo) throws Exception {
        int contador = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank() || linea.toLowerCase().startsWith("numero_cuenta")) continue;
                String[] partes = linea.split(",");
                if (partes.length >= 3) {
                    try {
                        inscribirAlumno(materia, partes[0].trim(), partes[1].trim(), partes[2].trim());
                        contador++;
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }
        return contador;
    }

    @Transactional
    public void quitarDeMateria(Long idInscripcion, Long idMateria) {
        Inscripcion ins = inscripcionRepo.findById(idInscripcion)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada."));
        if (!ins.getMateria().getIdMateria().equals(idMateria)) {
            throw new IllegalArgumentException("La inscripción no pertenece a esta materia.");
        }
        inscripcionRepo.delete(ins);
    }
}
