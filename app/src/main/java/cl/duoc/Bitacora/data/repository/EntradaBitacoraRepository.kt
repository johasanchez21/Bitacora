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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class EntradaBitacoraRepository(
    var dao: EntradaBitacoraDao? = null,
    private val apiService: BitacoraApiService = RetrofitClient.apiService
) {

    companion object {
        private val entradasMemoria = mutableListOf<EntradaBitacora>()
        private val _entradasMemoriaFlow = MutableStateFlow<List<EntradaBitacora>>(emptyList())

        @Volatile
        private var INSTANCE: EntradaBitacoraRepository? = null

        fun obtenerInstancia(dao: EntradaBitacoraDao? = null): EntradaBitacoraRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EntradaBitacoraRepository(dao).also { INSTANCE = it }
            }
        }

        private fun notificarCambioMemoria() {
            _entradasMemoriaFlow.value = synchronized(entradasMemoria) { entradasMemoria.toList() }
        }
    }

    suspend fun guardarEntrada(
        entrada: EntradaBitacora
    ): Long {
        if (entrada.estado == EstadoEntrada.ENVIADA) {
            var confirmadoPorServidor = false
            var entradaServidor = entrada

            try {
                val response = apiService.crearEntrada(entrada.toDto())
                if (response.isSuccessful && response.body() != null) {
                    entradaServidor = response.body()!!.toDomain()
                    confirmadoPorServidor = true
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
            }

            if (!confirmadoPorServidor) {
                val borradorLocal = entrada.copy(estado = EstadoEntrada.BORRADOR)
                val currentDao = dao
                if (currentDao != null) {
                    currentDao.insertarEntrada(borradorLocal.toEntity())
                } else {
                    synchronized(entradasMemoria) {
                        val nuevoId = (entradasMemoria.maxOfOrNull { it.id } ?: 0) + 1
                        entradasMemoria.add(borradorLocal.copy(id = nuevoId))
                        notificarCambioMemoria()
                    }
                }
                return -1L
            }

            val currentDao = dao
            if (currentDao != null) {
                val idLocal = currentDao.insertarEntrada(entradaServidor.toEntity())
                val idFinal = if (entradaServidor.id > 0) entradaServidor.id.toLong() else idLocal
                val guardada = entradaServidor.copy(id = idFinal.toInt())
                synchronized(entradasMemoria) {
                    val idx = entradasMemoria.indexOfFirst { it.id == guardada.id }
                    if (idx != -1) {
                        entradasMemoria[idx] = guardada
                    } else {
                        entradasMemoria.add(guardada)
                    }
                    notificarCambioMemoria()
                }
                return idFinal
            } else {
                synchronized(entradasMemoria) {
                    val nuevoId = if (entradaServidor.id > 0) entradaServidor.id else ((entradasMemoria.maxOfOrNull { it.id } ?: 0) + 1)
                    val e = entradaServidor.copy(id = nuevoId)
                    val idx = entradasMemoria.indexOfFirst { it.id == nuevoId }
                    if (idx != -1) {
                        entradasMemoria[idx] = e
                    } else {
                        entradasMemoria.add(e)
                    }
                    notificarCambioMemoria()
                    return nuevoId.toLong()
                }
            }
        }

        // Guardado BORRADOR local
        val currentDao = dao
        val idGenerado: Long = if (currentDao != null) {
            val entradaAInsertar = if (entrada.id > 0) entrada else entrada.copy(id = 0)
            val idLocal = currentDao.insertarEntrada(entradaAInsertar.toEntity())
            val idFinal = if (entradaAInsertar.id > 0) entradaAInsertar.id.toLong() else idLocal
            val guardada = entradaAInsertar.copy(id = idFinal.toInt())
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == guardada.id }
                if (idx != -1) {
                    entradasMemoria[idx] = guardada
                } else {
                    entradasMemoria.add(guardada)
                }
                notificarCambioMemoria()
            }
            idFinal
        } else {
            synchronized(entradasMemoria) {
                val nuevoId = if (entrada.id > 0) entrada.id else ((entradasMemoria.maxOfOrNull { it.id } ?: 0) + 1)
                val e = entrada.copy(id = nuevoId)
                val idx = entradasMemoria.indexOfFirst { it.id == nuevoId }
                if (idx != -1) {
                    entradasMemoria[idx] = e
                } else {
                    entradasMemoria.add(e)
                }
                notificarCambioMemoria()
                nuevoId.toLong()
            }
        }

        try {
            val dtoSinIdLocal = entrada.copy(id = idGenerado.toInt()).toDto()
            apiService.crearEntrada(dtoSinIdLocal)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }

        return idGenerado
    }

    fun obtenerEntradasPorEstudiante(
        estudianteId: Int
    ): Flow<List<EntradaBitacora>> = flow {
        val currentDao = dao
        if (currentDao != null) {
            currentDao.obtenerEntradasPorEstudiante(estudianteId).collect { locales ->
                val listaLocales = locales.map { it.toModel() }
                emit(listaLocales)

                try {
                    val response = apiService.obtenerEntradasPorEstudiante(estudianteId.toLong())
                    if (response.isSuccessful && response.body() != null) {
                        val remotas = response.body()!!.map { it.toDomain() }
                        val combinadas = combinarEntradas(remotas, listaLocales)
                        emit(combinadas)
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                }
            }
        } else {
            _entradasMemoriaFlow.collect { memoriaList ->
                val filtradas = memoriaList.filter { it.estudianteId == estudianteId }
                emit(filtradas)
            }
        }
    }

    private fun combinarEntradas(
        remotas: List<EntradaBitacora>,
        locales: List<EntradaBitacora>
    ): List<EntradaBitacora> {
        val mapa = LinkedHashMap<Int, EntradaBitacora>()
        for (r in remotas) {
            mapa[r.id] = r
        }
        for (l in locales) {
            mapa[l.id] = l
        }
        return mapa.values.toList()
    }

    suspend fun obtenerEntradaPorId(
        id: Int
    ): EntradaBitacora? {
        val currentDao = dao
        if (currentDao != null) {
            val local = currentDao.obtenerEntradaPorId(id)?.toModel()
            if (local != null) return local
        }
        return synchronized(entradasMemoria) {
            entradasMemoria.find { it.id == id }
        }
    }

    suspend fun actualizarBorrador(
        entrada: EntradaBitacora
    ): Boolean {
        val currentDao = dao
        if (currentDao != null) {
            currentDao.insertarEntrada(entrada.toEntity())
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == entrada.id }
                if (idx != -1) {
                    entradasMemoria[idx] = entrada
                } else {
                    entradasMemoria.add(entrada)
                }
                notificarCambioMemoria()
            }
        } else {
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == entrada.id }
                if (idx != -1) {
                    entradasMemoria[idx] = entrada
                } else {
                    entradasMemoria.add(entrada)
                }
                notificarCambioMemoria()
            }
        }

        try {
            apiService.crearEntrada(entrada.toDto())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }

        return true
    }

    suspend fun enviarBorrador(
        entrada: EntradaBitacora
    ): Boolean {
        val entradaEnviada = entrada.copy(estado = EstadoEntrada.ENVIADA)
        val currentDao = dao

        try {
            val response = apiService.crearEntrada(entradaEnviada.toDto())
            if (response.isSuccessful && response.body() != null) {
                val entradaFinal = response.body()!!.toDomain()
                if (currentDao != null) {
                    currentDao.insertarEntrada(entradaFinal.toEntity())
                }
                synchronized(entradasMemoria) {
                    val idx = entradasMemoria.indexOfFirst { it.id == entradaFinal.id }
                    if (idx != -1) {
                        entradasMemoria[idx] = entradaFinal
                    } else {
                        entradasMemoria.add(entradaFinal)
                    }
                    notificarCambioMemoria()
                }
                return true
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }

        // Si la llamada al backend no fue exitosa o no hubo conexion:
        val borradorLocal = entrada.copy(estado = EstadoEntrada.BORRADOR)
        if (currentDao != null) {
            currentDao.insertarEntrada(borradorLocal.toEntity())
        }
        synchronized(entradasMemoria) {
            val idx = entradasMemoria.indexOfFirst { it.id == borradorLocal.id }
            if (idx != -1) {
                entradasMemoria[idx] = borradorLocal
            } else {
                entradasMemoria.add(borradorLocal)
            }
            notificarCambioMemoria()
        }
        return false
    }

    fun obtenerEntradasEnviadas(): Flow<List<EntradaBitacora>> = flow {
        val currentDao = dao
        if (currentDao != null) {
            currentDao.obtenerEntradasEnviadas().collect { lista ->
                emit(lista.map { it.toModel() })
            }
        } else {
            _entradasMemoriaFlow.collect { list ->
                emit(list.filter { it.estado == EstadoEntrada.ENVIADA })
            }
        }

        try {
            val response = apiService.obtenerEntradasEnviadas()
            if (response.isSuccessful && response.body() != null) {
                emit(response.body()!!.map { it.toDomain() })
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }

    suspend fun revisarEntrada(
        entradaId: Int,
        comentarioProfesor: String
    ): Boolean {
        if (comentarioProfesor.isBlank()) return false
        val currentDao = dao

        if (currentDao != null) {
            val entradaExistente = currentDao.obtenerEntradaPorId(entradaId)
            if (entradaExistente != null) {
                val actualizada = entradaExistente.copy(
                    comentarioProfesor = comentarioProfesor.trim(),
                    estado = EstadoEntrada.REVISADA.name
                )
                currentDao.insertarEntrada(actualizada)
                synchronized(entradasMemoria) {
                    val idx = entradasMemoria.indexOfFirst { it.id == entradaId }
                    if (idx != -1) {
                        entradasMemoria[idx] = actualizada.toModel()
                    }
                    notificarCambioMemoria()
                }
            }
        } else {
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == entradaId }
                if (idx != -1) {
                    entradasMemoria[idx] = entradasMemoria[idx].copy(
                        comentarioProfesor = comentarioProfesor.trim(),
                        estado = EstadoEntrada.REVISADA
                    )
                    notificarCambioMemoria()
                }
            }
        }

        try {
            apiService.revisarEntrada(
                entradaId.toLong(),
                RevisarEntradaRequestDto(comentarioProfesor.trim())
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }

        return true
    }
}
