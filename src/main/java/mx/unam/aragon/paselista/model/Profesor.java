package mx.unam.aragon.paselista.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PROFESORES")
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profesor")
    private Long idProfesor;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    public Long getIdProfesor() { return idProfesor; }
    public void setIdProfesor(Long idProfesor) { this.idProfesor = idProfesor; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
