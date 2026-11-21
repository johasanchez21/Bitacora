package cl.duoc.bitacora.model;

import jakarta.persistence.*;

@Entity
@Table(name = "entradas_bitacora")
public class EntradaBitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bitacoraId;

    @Column(nullable = false)
    private Long estudianteId;

    @Column(nullable = false)
    private String fecha;

    @Column(nullable = false, length = 1000)
    private String actividad;

    @Column(nullable = false, length = 1000)
    private String aprendizaje;

    @Column(length = 1000)
    private String dudas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEntrada estado;

    @Column(length = 1000)
    private String comentarioProfesor;

    private String fotoUri;

    public EntradaBitacora() {}

    public EntradaBitacora(Long id, Long bitacoraId, Long estudianteId, String fecha, String actividad, String aprendizaje, String dudas, EstadoEntrada estado, String comentarioProfesor, String fotoUri) {
        this.id = id;
        this.bitacoraId = bitacoraId;
        this.estudianteId = estudianteId;
        this.fecha = fecha;
        this.actividad = actividad;
        this.aprendizaje = aprendizaje;
        this.dudas = dudas;
        this.estado = estado;
        this.comentarioProfesor = comentarioProfesor;
        this.fotoUri = fotoUri;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBitacoraId() { return bitacoraId; }
    public void setBitacoraId(Long bitacoraId) { this.bitacoraId = bitacoraId; }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public String getAprendizaje() { return aprendizaje; }
    public void setAprendizaje(String aprendizaje) { this.aprendizaje = aprendizaje; }

    public String getDudas() { return dudas; }
    public void setDudas(String dudas) { this.dudas = dudas; }

    public EstadoEntrada getEstado() { return estado; }
    public void setEstado(EstadoEntrada estado) { this.estado = estado; }

    public String getComentarioProfesor() { return comentarioProfesor; }
    public void setComentarioProfesor(String comentarioProfesor) { this.comentarioProfesor = comentarioProfesor; }

    public String getFotoUri() { return fotoUri; }
    public void setFotoUri(String fotoUri) { this.fotoUri = fotoUri; }
}
