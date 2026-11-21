package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.local.dao.EntradaBitacoraDao
import cl.duoc.Bitacora.data.mapper.toEntity
import cl.duoc.Bitacora.data.mapper.toModel
import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.remote.RetrofitClient
import cl.duoc.Bitacora.data.remote.dto.RevisarEntradaRequestDto
import cl.duoc.Bitacora.data.remote.mapper.toDomain
import cl.duoc.Bitacora.data.remote.mapper.toDto
import cl.duoc.Bitacora.domain.model.EntradaBitacora
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EntradaBitacoraRepository(
    private val dao: EntradaBitacoraDao? = null,
    private val apiService: BitacoraApiService = RetrofitClient.apiService
) {

    companion object {
        private val entradasMemoria = mutableListOf<EntradaBitacora>()

        @Volatile
        private var INSTANCE: EntradaBitacoraRepository? = null

        fun obtenerInstancia(dao: EntradaBitacoraDao? = null): EntradaBitacoraRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EntradaBitacoraRepository(dao).also { INSTANCE = it }
            }
        }
    }

    suspend fun guardarEntrada(
        entrada: EntradaBitacora
    ): Long {
        if (entrada.estado == EstadoEntrada.ENVIADA) {
            try {
                val response = apiService.crearEntrada(entrada.toDto())
                if (response.isSuccessful && response.body() != null) {
                    val guardada = response.body()!!.toDomain()
                    if (dao != null) {
                        dao.insertarEntrada(guardada.toEntity())
                    }
                    return guardada.id.toLong()
                }
            } catch (e: Exception) {
                // Fallback local
            }
        }

        if (dao != null) {
            return dao.insertarEntrada(entrada.toEntity())
        } else {
            synchronized(entradasMemoria) {
                val nuevoId = (entradasMemoria.maxOfOrNull { it.id } ?: 0) + 1
                val e = entrada.copy(id = nuevoId)
                entradasMemoria.add(e)
                return nuevoId.toLong()
            }
        }
    }

    fun obtenerEntradasPorEstudiante(
        estudianteId: Int
    ): Flow<List<EntradaBitacora>> = flow {
        var remotas = emptyList<EntradaBitacora>()
        try {
            val response = apiService.obtenerEntradasPorEstudiante(estudianteId.toLong())
            if (response.isSuccessful && response.body() != null) {
                remotas = response.body()!!.map { it.toDomain() }
            }
        } catch (e: Exception) {
            // Fallback
        }

        if (dao != null) {
            dao.obtenerEntradasPorEstudiante(estudianteId).collect { locales ->
                val combinadas = (remotas + locales.map { it.toModel() }).distinctBy { it.id }
                emit(combinadas)
            }
        } else {
            val combinadas = (remotas + synchronized(entradasMemoria) {
                entradasMemoria.filter { it.estudianteId == estudianteId }
            }).distinctBy { it.id }
            emit(combinadas)
        }
    }

    suspend fun obtenerEntradaPorId(
        id: Int
    ): EntradaBitacora? {
        if (dao != null) {
            val local = dao.obtenerEntradaPorId(id)?.toModel()
            if (local != null) return local
        }
        return synchronized(entradasMemoria) {
            entradasMemoria.find { it.id == id }
        }
    }

    suspend fun actualizarBorrador(
        entrada: EntradaBitacora
    ): Boolean {
        if (dao != null) {
            val entradaExistente = dao.obtenerEntradaPorId(entrada.id) ?: return false
            if (entradaExistente.estado != EstadoEntrada.BORRADOR.name) return false
            dao.actualizarEntrada(entrada.toEntity())
            return true
        } else {
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == entrada.id }
                if (idx != -1 && entradasMemoria[idx].estado == EstadoEntrada.BORRADOR) {
                    entradasMemoria[idx] = entrada
                    return true
                }
            }
            return false
        }
    }

    suspend fun enviarBorrador(
        entrada: EntradaBitacora
    ): Boolean {
        val entradaEnviada = entrada.copy(estado = EstadoEntrada.ENVIADA)
        try {
            val response = apiService.crearEntrada(entradaEnviada.toDto())
            if (response.isSuccessful) {
                if (dao != null) {
                    dao.actualizarEntrada(entradaEnviada.toEntity())
                }
                return true
            }
        } catch (e: Exception) {
            // Fallback
        }

        if (dao != null) {
            dao.actualizarEntrada(entradaEnviada.toEntity())
            return true
        } else {
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == entrada.id }
                if (idx != -1) {
                    entradasMemoria[idx] = entradaEnviada
                    return true
                }
            }
            return false
        }
    }

    fun obtenerEntradasEnviadas(): Flow<List<EntradaBitacora>> = flow {
        try {
            val response = apiService.obtenerEntradasEnviadas()
            if (response.isSuccessful && response.body() != null) {
                emit(response.body()!!.map { it.toDomain() })
                return@flow
            }
        } catch (e: Exception) {
            // Fallback
        }

        if (dao != null) {
            dao.obtenerEntradasEnviadas().collect { lista ->
                emit(lista.map { it.toModel() })
            }
        } else {
            emit(synchronized(entradasMemoria) {
                entradasMemoria.filter { it.estado == EstadoEntrada.ENVIADA }
            })
        }
    }

    suspend fun revisarEntrada(
        entradaId: Int,
        comentarioProfesor: String
    ): Boolean {
        if (comentarioProfesor.isBlank()) return false
        try {
            val response = apiService.revisarEntrada(
                entradaId.toLong(),
                RevisarEntradaRequestDto(comentarioProfesor.trim())
            )
            if (response.isSuccessful) {
                return true
            }
        } catch (e: Exception) {
            // Fallback
        }

        if (dao != null) {
            val entradaExistente = dao.obtenerEntradaPorId(entradaId) ?: return false
            dao.actualizarEntrada(
                entradaExistente.copy(
                    comentarioProfesor = comentarioProfesor.trim(),
                    estado = EstadoEntrada.REVISADA.name
                )
            )
            return true
        } else {
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == entradaId }
                if (idx != -1) {
                    entradasMemoria[idx] = entradasMemoria[idx].copy(
                        comentarioProfesor = comentarioProfesor.trim(),
                        estado = EstadoEntrada.REVISADA
                    )
                    return true
                }
            }
            return false
        }
    }
}
