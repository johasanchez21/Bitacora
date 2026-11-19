package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.local.dao.CursoDao
import cl.duoc.Bitacora.data.mapper.toEntity
import cl.duoc.Bitacora.data.mapper.toModel
import cl.duoc.Bitacora.domain.model.Curso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CursoRepository(
    private val cursoDao: CursoDao? = null
) {

    companion object {
        private val cursosMemoria = mutableListOf(
            Curso(id = 1, nombre = "1° Medio A", codigo = "1MA", anio = 2025, semestre = 1),
            Curso(id = 2, nombre = "2° Medio B", codigo = "2MB", anio = 2025, semestre = 2)
        )

        @Volatile
        private var INSTANCE: CursoRepository? = null

        fun obtenerInstancia(cursoDao: CursoDao? = null): CursoRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CursoRepository(cursoDao).also { INSTANCE = it }
            }
        }
    }

    /**
     * Regla de Negocio 1: Los cursos deben estar registrados y solo pueden ser máximo dos por año.
     */
    suspend fun registrarCurso(curso: Curso): Result<Long> {
        if (curso.nombre.isBlank() || curso.codigo.isBlank()) {
            return Result.failure(
                IllegalArgumentException("El nombre y código del curso son obligatorios.")
            )
        }

        val cantidadEnAnio = if (cursoDao != null) {
            cursoDao.contarCursosPorAnio(curso.anio)
        } else {
            synchronized(cursosMemoria) {
                cursosMemoria.count { it.anio == curso.anio }
            }
        }

        if (cantidadEnAnio >= 2) {
            return Result.failure(
                IllegalArgumentException("No se pueden registrar más de 2 cursos por año (El año ${curso.anio} ya tiene $cantidadEnAnio cursos registrados).")
            )
        }

        if (cursoDao != null) {
            val id = cursoDao.insertarCurso(curso.toEntity())
            val cursoConId = curso.copy(id = id.toInt())
            synchronized(cursosMemoria) {
                cursosMemoria.add(cursoConId)
            }
            return Result.success(id)
        } else {
            synchronized(cursosMemoria) {
                val nuevoId = (cursosMemoria.maxOfOrNull { it.id } ?: 0) + 1
                val cursoGuardado = curso.copy(id = nuevoId)
                cursosMemoria.add(cursoGuardado)
                return Result.success(nuevoId.toLong())
            }
        }
    }

    fun obtenerTodosLosCursos(): Flow<List<Curso>> {
        return if (cursoDao != null) {
            cursoDao.obtenerTodosLosCursos().map { lista ->
                lista.map { it.toModel() }
            }
        } else {
            flowOf(cursosMemoria)
        }
    }

    suspend fun obtenerCursosPorAnio(anio: Int): List<Curso> {
        return if (cursoDao != null) {
            cursoDao.obtenerCursosPorAnio(anio).map { it.toModel() }
        } else {
            synchronized(cursosMemoria) {
                cursosMemoria.filter { it.anio == anio }
            }
        }
    }

    suspend fun obtenerCursoPorId(id: Int): Curso? {
        return if (cursoDao != null) {
            cursoDao.obtenerCursoPorId(id)?.toModel()
        } else {
            synchronized(cursosMemoria) {
                cursosMemoria.find { it.id == id }
            }
        }
    }
}
