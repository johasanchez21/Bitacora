package cl.duoc.bitacora.controller;

import cl.duoc.bitacora.model.EntradaBitacora;
import cl.duoc.bitacora.model.EstadoEntrada;
import cl.duoc.bitacora.repository.EntradaBitacoraRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entradas")
@CrossOrigin(origins = "*")
public class EntradaBitacoraController {

    private final EntradaBitacoraRepository entradaRepository;

    public EntradaBitacoraController(EntradaBitacoraRepository entradaRepository) {
        this.entradaRepository = entradaRepository;
    }

    @GetMapping
    public List<EntradaBitacora> listarTodas() {
        return entradaRepository.findAll();
    }

    @GetMapping("/estudiante/{estudianteId}")
    public List<EntradaBitacora> listarPorEstudiante(@PathVariable Long estudianteId) {
        return entradaRepository.findByEstudianteIdOrderByIdDesc(estudianteId);
    }

    @GetMapping("/enviadas")
    public List<EntradaBitacora> listarEnviadas() {
        return entradaRepository.findByEstadoOrderByIdDesc(EstadoEntrada.ENVIADA);
    }

    @PostMapping
    public ResponseEntity<?> crearEntrada(@Valid @RequestBody EntradaBitacora entrada) {
        if (entrada.getEstado() == null) {
            entrada.setEstado(EstadoEntrada.BORRADOR);
        }
        EntradaBitacora guardada = entradaRepository.save(entrada);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}/revisar")
    public ResponseEntity<?> revisarEntrada(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String comentario = body.get("comentario");
        if (comentario == null || comentario.isBlank()) {
            return ResponseEntity.badRequest().body("El comentario de revisión es obligatorio");
        }
        return entradaRepository.findById(id).map(e -> {
            e.setComentarioProfesor(comentario.trim());
            e.setEstado(EstadoEntrada.REVISADA);
            return ResponseEntity.ok(entradaRepository.save(e));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
