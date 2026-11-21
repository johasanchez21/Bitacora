package cl.duoc.bitacora.repository;

import cl.duoc.bitacora.model.EntradaBitacora;
import cl.duoc.bitacora.model.EstadoEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaBitacoraRepository extends JpaRepository<EntradaBitacora, Long> {
    List<EntradaBitacora> findByEstudianteIdOrderByIdDesc(Long estudianteId);
    List<EntradaBitacora> findByEstadoOrderByIdDesc(EstadoEntrada estado);
}
