package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.local.dao.CursoDao
import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.remote.RetrofitClient
import cl.duoc.Bitacora.data.remote.mapper.toDomain
import cl.duoc.Bitacora.data.remote.mapper.toDto
import cl.duoc.Bitacora.domain.model.Curso
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart

class CursoRepository(
    private val cursoDao: CursoDao? = null,
    private val apiService: BitacoraApiService = RetrofitClient.apiService
) {

    companion object {
        private val cursosPorDefecto = listOf(
            Curso(id = 1, nombre = "1° Medio A", codigo = "1MA", anio = 2025, semestre = 1),
            Curso(id = 2, nombre = "1° Medio B", codigo = "1MB", anio = 2025, semestre = 1)
        )

        private val cursosMemoria = mutableListOf<Curso>().apply { addAll(cursosPorDefecto) }
        private val _cursosFlow = MutableStateFlow<List<Curso>>(cursosMemoria.toList())

        @Volatile
        private var INSTANCE: CursoRepository? = null

        fun obtenerInstancia(cursoDao: CursoDao? = null): CursoRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CursoRepository(cursoDao).also { INSTANCE = it }
            }
        }

        private fun emitirCambio() {
            _cursosFlow.value = synchronized(cursosMemoria) { cursosMemoria.toList() }
        }
    }

    suspend fun refrescarCursosDesdeServidor() {
        try {
            val response = apiService.obtenerCursos()
            if (response.isSuccessful && response.body() != null) {
                val listaRemota = response.body()!!.map { it.toDomain() }
                if (listaRemota.isNotEmpty()) {
                    synchronized(cursosMemoria) {
                        cursosMemoria.clear()
                        cursosMemoria.addAll(listaRemota)
                    }
                    emitirCambio()
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }

    suspend fun registrarCurso(curso: Curso): Result<Long> {
        if (curso.nombre.isBlank() || curso.codigo.isBlank()) {
            return Result.failure(
                IllegalArgumentException("El nombre y código del curso son obligatorios.")
            )
        }

        val cantidadEnAnio = synchronized(cursosMemoria) {
            cursosMemoria.count { it.anio == curso.anio }
        }

        if (cantidadEnAnio >= 2) {
            return Result.failure(
                IllegalArgumentException("No se pueden registrar más de 2 cursos por año (El año ${curso.anio} ya tiene $cantidadEnAnio cursos registrados).")
            )
        }

        try {
            val response = apiService.registrarCurso(curso.copy(id = 0).toDto())
            if (response.isSuccessful && response.body() != null) {
                val cursoGuardado = response.body()!!.toDomain()
                synchronized(cursosMemoria) {
                    val idx = cursosMemoria.indexOfFirst { it.id == cursoGuardado.id }
                    if (idx != -1) cursosMemoria[idx] = cursoGuardado else cursosMemoria.add(cursoGuardado)
                }
                emitirCambio()
                return Result.success(cursoGuardado.id.toLong())
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }

        synchronized(cursosMemoria) {
            val nuevoId = (cursosMemoria.maxOfOrNull { it.id } ?: 0) + 1
            val cursoGuardado = curso.copy(id = nuevoId)
            cursosMemoria.add(cursoGuardado)
            emitirCambio()
            return Result.success(nuevoId.toLong())
        }
    }

    fun obtenerTodosLosCursos(): Flow<List<Curso>> = _cursosFlow.asStateFlow().onStart {
        refrescarCursosDesdeServidor()
    }

    suspend fun obtenerCursosPorAnio(anio: Int): List<Curso> {
        refrescarCursosDesdeServidor()
        return synchronized(cursosMemoria) { cursosMemoria.filter { it.anio == anio } }
    }

    suspend fun obtenerCursoPorId(id: Int): Curso? {
        return synchronized(cursosMemoria) {
            cursosMemoria.find { it.id == id }
        }
    }
}