package mx.unam.aragon.paselista.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "MATERIAS")
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia")
    private Long idMateria;

    @Column(name = "nombre_materia", nullable = false, length = 100)
    private String nombreMateria;

    @Column(name = "clave_materia", nullable = false, length = 20)
    private String claveMateria;

    @Column(name = "grupo", nullable = false, length = 10)
    private String grupo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesor", nullable = false)
    private Profesor profesor;

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioMateria> horarios = new ArrayList<>();

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public String getResumenHorario() {
        if (horarios == null || horarios.isEmpty()) return "Sin horario";
        String dias = horarios.stream()
                .map(HorarioMateria::getNombreDia)
                .collect(Collectors.joining(", "));
        HorarioMateria primero = horarios.get(0);
        return dias + " (" + primero.getHoraInicio() + " - " + primero.getHoraFin() + ")";
    }

    public Long getIdMateria() { return idMateria; }
    public void setIdMateria(Long idMateria) { this.idMateria = idMateria; }
    public String getNombreMateria() { return nombreMateria; }
    public void setNombreMateria(String nombreMateria) { this.nombreMateria = nombreMateria; }
    public String getClaveMateria() { return claveMateria; }
    public void setClaveMateria(String claveMateria) { this.claveMateria = claveMateria; }
    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public Profesor getProfesor() { return profesor; }
    public void setProfesor(Profesor profesor) { this.profesor = profesor; }
    public List<HorarioMateria> getHorarios() { return horarios; }
    public void setHorarios(List<HorarioMateria> horarios) { this.horarios = horarios; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }
    public void setInscripciones(List<Inscripcion> inscripciones) { this.inscripciones = inscripciones; }
}
