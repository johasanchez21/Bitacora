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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class BitacoraRepository(
    private val bitacoraDao: BitacoraDao? = null,
    private val apiService: BitacoraApiService = RetrofitClient.apiService
) {

    companion object {
        private val bitacorasPorDefecto = listOf(
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

        private val bitacorasMemoria = mutableListOf<Bitacora>().apply { addAll(bitacorasPorDefecto) }
        private val _bitacorasFlow = MutableStateFlow<List<Bitacora>>(bitacorasMemoria.toList())

        @Volatile
        private var INSTANCE: BitacoraRepository? = null

        fun obtenerInstancia(bitacoraDao: BitacoraDao? = null): BitacoraRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BitacoraRepository(bitacoraDao).also { INSTANCE = it }
            }
        }

        private fun emitirCambio() {
            _bitacorasFlow.value = synchronized(bitacorasMemoria) { bitacorasMemoria.toList() }
        }
    }

    suspend fun refrescarBitacorasDesdeServidor() {
        try {
            val response = apiService.obtenerTodasLasBitacoras()
            if (response.isSuccessful && response.body() != null) {
                val listaRemota = response.body()!!.map { it.toDomain() }
                synchronized(bitacorasMemoria) {
                    bitacorasMemoria.clear()
                    if (listaRemota.isNotEmpty()) {
                        bitacorasMemoria.addAll(listaRemota)
                    } else {
                        bitacorasMemoria.addAll(bitacorasPorDefecto)
                    }
                }
                emitirCambio()
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
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
            val response = apiService.crearBitacora(bitacoraAAsignar.copy(id = 0).toDto())
            if (response.isSuccessful && response.body() != null) {
                val b = response.body()!!.toDomain()
                synchronized(bitacorasMemoria) {
                    val idx = bitacorasMemoria.indexOfFirst { it.id == b.id }
                    if (idx != -1) bitacorasMemoria[idx] = b else bitacorasMemoria.add(b)
                }
                if (bitacoraDao != null) {
                    bitacoraDao.insertarBitacora(b.toEntity())
                }
                emitirCambio()
                return Result.success(b.id.toLong())
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }

        val nuevoId = (bitacorasMemoria.maxOfOrNull { it.id } ?: 0) + 1
        val bitacoraGuardada = bitacoraAAsignar.copy(id = nuevoId)
        synchronized(bitacorasMemoria) { bitacorasMemoria.add(bitacoraGuardada) }
        if (bitacoraDao != null) {
            bitacoraDao.insertarBitacora(bitacoraGuardada.toEntity())
        }
        emitirCambio()
        return Result.success(nuevoId.toLong())
    }

    suspend fun guardarBitacora(bitacora: Bitacora): Long {
        try {
            val response = apiService.crearBitacora(bitacora.copy(id = 0).toDto())
            if (response.isSuccessful && response.body() != null) {
                val b = response.body()!!.toDomain()
                synchronized(bitacorasMemoria) {
                    val idx = bitacorasMemoria.indexOfFirst { it.id == b.id }
                    if (idx != -1) bitacorasMemoria[idx] = b else bitacorasMemoria.add(b)
                }
                if (bitacoraDao != null) {
                    bitacoraDao.insertarBitacora(b.toEntity())
                }
                emitirCambio()
                return b.id.toLong()
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }

        val nuevoId = (bitacorasMemoria.maxOfOrNull { it.id } ?: 0) + 1
        val b = bitacora.copy(id = nuevoId)
        synchronized(bitacorasMemoria) { bitacorasMemoria.add(b) }
        if (bitacoraDao != null) {
            bitacoraDao.insertarBitacora(b.toEntity())
        }
        emitirCambio()
        return nuevoId.toLong()
    }

    suspend fun obtenerBitacoraPorIdRoom(id: Int): Bitacora? {
        refrescarBitacorasDesdeServidor()
        if (bitacoraDao != null) {
            val entity = bitacoraDao.obtenerBitacoraPorId(id)
            if (entity != null) return entity.toModel()
        }
        return synchronized(bitacorasMemoria) { bitacorasMemoria.find { it.id == id } }
    }

    fun obtenerTodasLasBitacorasRoom(): Flow<List<Bitacora>> = _bitacorasFlow.asStateFlow().onStart {
        refrescarBitacorasDesdeServidor()
    }

    fun obtenerBitacorasPublicadasRoom(curso: String): Flow<List<Bitacora>> =
        obtenerTodasLasBitacorasRoom().map { lista ->
            lista.filter { it.curso == curso && it.estado == EstadoBitacora.PUBLICADA }
        }

    suspend fun publicarBitacora(id: Int): Boolean {
        try {
            val response = apiService.publicarBitacora(id.toLong())
            if (response.isSuccessful && response.body() != null) {
                refrescarBitacorasDesdeServidor()
                return true
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
        synchronized(bitacorasMemoria) {
            val idx = bitacorasMemoria.indexOfFirst { it.id == id }
            if (idx != -1 && bitacorasMemoria[idx].estado == EstadoBitacora.BORRADOR) {
                bitacorasMemoria[idx] = bitacorasMemoria[idx].copy(estado = EstadoBitacora.PUBLICADA)
                emitirCambio()
                return true
            }
        }
        return false
    }
}