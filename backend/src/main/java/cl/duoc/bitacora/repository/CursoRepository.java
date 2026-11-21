package cl.duoc.bitacora.repository;

import cl.duoc.bitacora.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    long countByAnio(Integer anio);
    List<Curso> findByAnio(Integer anio);
}
