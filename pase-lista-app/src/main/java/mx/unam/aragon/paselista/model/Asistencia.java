package mx.unam.aragon.paselista.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "ASISTENCIAS",
    uniqueConstraints = @UniqueConstraint(
        name = "UNIQUE_SESION_ALUMNO",
        columnNames = {"id_inscripcion", "fecha_sesion"}
    )
)
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long idAsistencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscripcion", nullable = false)
    private Inscripcion inscripcion;

    @Column(name = "fecha_sesion", nullable = false)
    private LocalDate fechaSesion;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    public Long getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Long idAsistencia) { this.idAsistencia = idAsistencia; }
    public Inscripcion getInscripcion() { return inscripcion; }
    public void setInscripcion(Inscripcion inscripcion) { this.inscripcion = inscripcion; }
    public LocalDate getFechaSesion() { return fechaSesion; }
    public void setFechaSesion(LocalDate fechaSesion) { this.fechaSesion = fechaSesion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
