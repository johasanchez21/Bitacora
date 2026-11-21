package cl.duoc.bitacora.controller;

import cl.duoc.bitacora.model.Bitacora;
import cl.duoc.bitacora.model.EstadoBitacora;
import cl.duoc.bitacora.repository.BitacoraRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bitacoras")
@CrossOrigin(origins = "*")
public class BitacoraController {

    private final BitacoraRepository bitacoraRepository;

    public BitacoraController(BitacoraRepository bitacoraRepository) {
        this.bitacoraRepository = bitacoraRepository;
    }

    @GetMapping
    public List<Bitacora> listarTodas() {
        return bitacoraRepository.findAll();
    }

    @GetMapping("/curso/{cursoId}")
    public List<Bitacora> listarPorCurso(@PathVariable Long cursoId) {
        return bitacoraRepository.findByCursoId(cursoId);
    }

    @PostMapping
    public ResponseEntity<?> crearBitacora(@Valid @RequestBody Bitacora bitacora) {
        if (bitacora.getCursoId() == null || bitacora.getCursoId() <= 0) {
            return ResponseEntity.badRequest().body("La bitácora debe ser asignada a un curso en particular");
        }
        if (bitacora.getEstado() == null) {
            bitacora.setEstado(EstadoBitacora.PUBLICADA);
        }
        Bitacora guardada = bitacoraRepository.save(bitacora);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}/publicar")
    public ResponseEntity<?> publicarBitacora(@PathVariable Long id) {
        return bitacoraRepository.findById(id).map(b -> {
            b.setEstado(EstadoBitacora.PUBLICADA);
            return ResponseEntity.ok(bitacoraRepository.save(b));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
