package cl.duoc.bitacora.controller;

import cl.duoc.bitacora.model.Curso;
import cl.duoc.bitacora.repository.CursoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {

    private final CursoRepository cursoRepository;

    public CursoController(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @GetMapping
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    @GetMapping("/anio/{anio}")
    public List<Curso> listarPorAnio(@PathVariable Integer anio) {
        return cursoRepository.findByAnio(anio);
    }

    @PostMapping
    public ResponseEntity<?> registrarCurso(@Valid @RequestBody Curso curso) {
        if (curso.getNombre() == null || curso.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del curso es obligatorio");
        }
        long cursosEnAnio = cursoRepository.countByAnio(curso.getAnio());
        if (cursosEnAnio >= 2) {
            return ResponseEntity.badRequest().body("No se pueden registrar más de 2 cursos por año (El año " + curso.getAnio() + " ya tiene " + cursosEnAnio + " cursos).");
        }
        Curso guardado = cursoRepository.save(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}
