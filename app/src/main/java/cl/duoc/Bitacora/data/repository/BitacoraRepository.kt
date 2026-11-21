package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.local.dao.BitacoraDao
import cl.duoc.Bitacora.data.mapper.toEntity
import cl.duoc.Bitacora.data.mapper.toModel
import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.remote.RetrofitClient
import cl.duoc.Bitacora.data.remote.mapper.toDomain
import cl.duoc.Bitacora.data.remote.mapper.toDto
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.EstadoBitacora
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BitacoraRepository(
    private val bitacoraDao: BitacoraDao? = null,
    private val apiService: BitacoraApiService = RetrofitClient.apiService
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

        try {
            val response = apiService.crearBitacora(bitacoraAAsignar.toDto())
            if (response.isSuccessful && response.body() != null) {
                val b = response.body()!!.toDomain()
                synchronized(bitacorasMemoria) { bitacorasMemoria.add(b) }
                if (bitacoraDao != null) {
                    bitacoraDao.insertarBitacora(b.toEntity())
                }
                return Result.success(b.id.toLong())
            }
        } catch (e: Exception) {
            // Fallback
        }

        val nuevoId = (bitacorasMemoria.maxOfOrNull { it.id } ?: 0) + 1
        val bitacoraGuardada = bitacoraAAsignar.copy(id = nuevoId)
        synchronized(bitacorasMemoria) { bitacorasMemoria.add(bitacoraGuardada) }
        if (bitacoraDao != null) {
            bitacoraDao.insertarBitacora(bitacoraGuardada.toEntity())
        }
        return Result.success(nuevoId.toLong())
    }

    suspend fun guardarBitacora(bitacora: Bitacora): Long {
        try {
            val response = apiService.crearBitacora(bitacora.toDto())
            if (response.isSuccessful && response.body() != null) {
                val b = response.body()!!.toDomain()
                synchronized(bitacorasMemoria) { bitacorasMemoria.add(b) }
                if (bitacoraDao != null) {
                    bitacoraDao.insertarBitacora(b.toEntity())
                }
                return b.id.toLong()
            }
        } catch (e: Exception) {
            // Fallback
        }

        val nuevoId = (bitacorasMemoria.maxOfOrNull { it.id } ?: 0) + 1
        val b = bitacora.copy(id = nuevoId)
        synchronized(bitacorasMemoria) { bitacorasMemoria.add(b) }
        if (bitacoraDao != null) {
            bitacoraDao.insertarBitacora(b.toEntity())
        }
        return nuevoId.toLong()
    }

    suspend fun obtenerBitacoraPorIdRoom(id: Int): Bitacora? {
        try {
            val response = apiService.obtenerTodasLasBitacoras()
            if (response.isSuccessful && response.body() != null) {
                val encontrada = response.body()!!.map { it.toDomain() }.find { it.id == id }
                if (encontrada != null) return encontrada
            }
        } catch (e: Exception) {
            // Fallback
        }
        if (bitacoraDao != null) {
            val entity = bitacoraDao.obtenerBitacoraPorId(id)
            if (entity != null) return entity.toModel()
        }
        return synchronized(bitacorasMemoria) { bitacorasMemoria.find { it.id == id } }
    }

    fun obtenerTodasLasBitacorasRoom(): Flow<List<Bitacora>> = flow {
        try {
            val response = apiService.obtenerTodasLasBitacoras()
            if (response.isSuccessful && response.body() != null) {
                val lista = response.body()!!.map { it.toDomain() }
                synchronized(bitacorasMemoria) {
                    bitacorasMemoria.clear()
                    bitacorasMemoria.addAll(lista)
                }
                emit(lista)
                return@flow
            }
        } catch (e: Exception) {
            // Fallback
        }
        if (bitacoraDao != null) {
            bitacoraDao.obtenerTodasLasBitacoras().collect { entidades ->
                emit(entidades.map { it.toModel() })
            }
        } else {
            emit(synchronized(bitacorasMemoria) { bitacorasMemoria.toList() })
        }
    }

    fun obtenerBitacorasPublicadasRoom(curso: String): Flow<List<Bitacora>> = flow {
        try {
            val response = apiService.obtenerTodasLasBitacoras()
            if (response.isSuccessful && response.body() != null) {
                val filtradas = response.body()!!
                    .map { it.toDomain() }
                    .filter { it.curso == curso && it.estado == EstadoBitacora.PUBLICADA }
                emit(filtradas)
                return@flow
            }
        } catch (e: Exception) {
            // Fallback
        }
        emit(synchronized(bitacorasMemoria) {
            bitacorasMemoria.filter { it.curso == curso && it.estado == EstadoBitacora.PUBLICADA }
        })
    }

    suspend fun publicarBitacora(id: Int): Boolean {
        try {
            val response = apiService.publicarBitacora(id.toLong())
            if (response.isSuccessful && response.body() != null) {
                return true
            }
        } catch (e: Exception) {
            // Fallback
        }
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
