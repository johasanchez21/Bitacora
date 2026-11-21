package cl.duoc.bitacora.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bitacoras")
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String asignatura;

    @Column(nullable = false)
    private String mes;

    @Column(nullable = false)
    private Integer anio;

    private Long cursoId;

    @Column(nullable = false)
    private String curso;

    private Long profesorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBitacora estado;

    public Bitacora() {}

    public Bitacora(Long id, String asignatura, String mes, Integer anio, Long cursoId, String curso, Long profesorId, EstadoBitacora estado) {
        this.id = id;
        this.asignatura = asignatura;
        this.mes = mes;
        this.anio = anio;
        this.cursoId = cursoId;
        this.curso = curso;
        this.profesorId = profesorId;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public Long getProfesorId() { return profesorId; }
    public void setProfesorId(Long profesorId) { this.profesorId = profesorId; }

    public EstadoBitacora getEstado() { return estado; }
    public void setEstado(EstadoBitacora estado) { this.estado = estado; }
}
