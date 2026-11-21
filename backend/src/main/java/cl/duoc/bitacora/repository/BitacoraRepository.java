package cl.duoc.bitacora.repository;

import cl.duoc.bitacora.model.Bitacora;
import cl.duoc.bitacora.model.EstadoBitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {
    List<Bitacora> findByCursoId(Long cursoId);
    List<Bitacora> findByCursoAndEstado(String curso, EstadoBitacora estado);
}
