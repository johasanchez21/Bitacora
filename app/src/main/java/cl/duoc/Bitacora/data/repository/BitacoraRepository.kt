package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.local.dao.BitacoraDao
import cl.duoc.Bitacora.data.mapper.toEntity
import cl.duoc.Bitacora.data.mapper.toModel
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.EstadoBitacora
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class BitacoraRepository(
    private val bitacoraDao: BitacoraDao? = null
) {

    companion object {
        private val bitacorasMemoria = mutableListOf(
            Bitacora(
                id = 1,
                asignatura = "Matemáticas",
                mes = "Marzo",
                anio = 2025,
                cursoId = 1,
                curso = "1° Medio A",
                profesorId = 2,
                estado = EstadoBitacora.PUBLICADA
            )
        )

        @Volatile
        private var INSTANCE: BitacoraRepository? = null

        fun obtenerInstancia(bitacoraDao: BitacoraDao? = null): BitacoraRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BitacoraRepository(bitacoraDao).also { INSTANCE = it }
            }
        }
    }

    /**
     * Regla de Negocio 3: Las bitácoras deben ser asignadas a un curso en particular por el profesor.
     */
    suspend fun asignarBitacoraPorProfesor(
        profesor: Usuario,
        bitacora: Bitacora,
        curso: Curso
    ): Result<Long> {
        if (profesor.rol != RolUsuario.Profesor) {
            return Result.failure(
                IllegalArgumentException("Solo los profesores pueden crear y asignar bitácoras.")
            )
        }

        if (curso.id <= 0) {
            return Result.failure(
                IllegalArgumentException("La bitácora debe ser asignada a un curso en particular registrado.")
            )
        }

        val bitacoraAAsignar = bitacora.copy(
            cursoId = curso.id,
            curso = curso.nombre,
            profesorId = profesor.id
        )

        if (bitacoraDao != null) {
            val id = bitacoraDao.insertarBitacora(bitacoraAAsignar.toEntity())
            val bitacoraConId = bitacoraAAsignar.copy(id = id.toInt())
            synchronized(bitacorasMemoria) {
                bitacorasMemoria.add(bitacoraConId)
            }
            return Result.success(id)
        } else {
            synchronized(bitacorasMemoria) {
                val nuevoId = (bitacorasMemoria.maxOfOrNull { it.id } ?: 0) + 1
                val bitacoraGuardada = bitacoraAAsignar.copy(id = nuevoId)
                bitacorasMemoria.add(bitacoraGuardada)
                return Result.success(nuevoId.toLong())
            }
        }
    }

    suspend fun guardarBitacora(
        bitacora: Bitacora
    ): Long {
        if (bitacoraDao != null) {
            return bitacoraDao.insertarBitacora(bitacora.toEntity())
        } else {
            synchronized(bitacorasMemoria) {
                val nuevoId = (bitacorasMemoria.maxOfOrNull { it.id } ?: 0) + 1
                val b = bitacora.copy(id = nuevoId)
                bitacorasMemoria.add(b)
                return nuevoId.toLong()
            }
        }
    }

    fun obtenerTodasLasBitacorasRoom(): Flow<List<Bitacora>> {
        return if (bitacoraDao != null) {
            bitacoraDao.obtenerTodasLasBitacoras()
                .map { entidades -> entidades.map { it.toModel() } }
        } else {
            flowOf(bitacorasMemoria)
        }
    }

    fun obtenerBitacorasPublicadasRoom(
        curso: String
    ): Flow<List<Bitacora>> {
        return if (bitacoraDao != null) {
            bitacoraDao.obtenerBitacorasPublicadas(curso)
                .map { entidades -> entidades.map { it.toModel() } }
        } else {
            flowOf(bitacorasMemoria.filter { it.curso == curso && it.estado == EstadoBitacora.PUBLICADA })
        }
    }

    fun obtenerBitacorasPorCursoId(
        cursoId: Int
    ): Flow<List<Bitacora>> {
        return if (bitacoraDao != null) {
            bitacoraDao.obtenerBitacorasPorCursoId(cursoId)
                .map { entidades -> entidades.map { it.toModel() } }
        } else {
            flowOf(bitacorasMemoria.filter { it.cursoId == cursoId })
        }
    }

    suspend fun obtenerBitacoraPorIdRoom(
        id: Int
    ): Bitacora? {
        return if (bitacoraDao != null) {
            bitacoraDao.obtenerBitacoraPorId(id)?.toModel()
        } else {
            synchronized(bitacorasMemoria) {
                bitacorasMemoria.find { it.id == id }
            }
        }
    }

    suspend fun publicarBitacora(
        id: Int
    ): Boolean {
        if (bitacoraDao != null) {
            val bitacora = bitacoraDao.obtenerBitacoraPorId(id) ?: return false
            if (bitacora.estado != EstadoBitacora.BORRADOR.name) return false
            return bitacoraDao.actualizarEstado(id = id, nuevoEstado = EstadoBitacora.PUBLICADA.name) > 0
        } else {
            synchronized(bitacorasMemoria) {
                val idx = bitacorasMemoria.indexOfFirst { it.id == id }
                if (idx != -1 && bitacorasMemoria[idx].estado == EstadoBitacora.BORRADOR) {
                    bitacorasMemoria[idx] = bitacorasMemoria[idx].copy(estado = EstadoBitacora.PUBLICADA)
                    return true
                }
            }
            return false
        }
    }
}
