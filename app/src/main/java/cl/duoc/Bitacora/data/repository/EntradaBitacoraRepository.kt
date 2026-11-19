package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.local.dao.EntradaBitacoraDao
import cl.duoc.Bitacora.data.mapper.toEntity
import cl.duoc.Bitacora.data.mapper.toModel
import cl.duoc.Bitacora.domain.model.EntradaBitacora
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class EntradaBitacoraRepository(
    private val dao: EntradaBitacoraDao? = null
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
    ): Flow<List<EntradaBitacora>> {
        return if (dao != null) {
            dao.obtenerEntradasPorEstudiante(estudianteId)
                .map { lista -> lista.map { entidad -> entidad.toModel() } }
        } else {
            flowOf(entradasMemoria.filter { it.estudianteId == estudianteId })
        }
    }

    suspend fun obtenerEntradaPorId(
        id: Int
    ): EntradaBitacora? {
        return if (dao != null) {
            dao.obtenerEntradaPorId(id)?.toModel()
        } else {
            synchronized(entradasMemoria) {
                entradasMemoria.find { it.id == id }
            }
        }
    }

    suspend fun actualizarEstado(
        id: Int,
        nuevoEstado: EstadoEntrada
    ): Boolean {
        if (dao != null) {
            val entrada = dao.obtenerEntradaPorId(id) ?: return false
            dao.actualizarEntrada(entrada.copy(estado = nuevoEstado.name))
            return true
        } else {
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == id }
                if (idx != -1) {
                    entradasMemoria[idx] = entradasMemoria[idx].copy(estado = nuevoEstado)
                    return true
                }
            }
            return false
        }
    }

    suspend fun actualizarBorrador(
        entrada: EntradaBitacora
    ): Boolean {
        if (dao != null) {
            val entradaExistente = dao.obtenerEntradaPorId(entrada.id) ?: return false
            if (entradaExistente.estado != EstadoEntrada.BORRADOR.name) return false
            if (entradaExistente.estudianteId != entrada.estudianteId ||
                entradaExistente.bitacoraId != entrada.bitacoraId) {
                return false
            }
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
        if (dao != null) {
            val entradaExistente = dao.obtenerEntradaPorId(entrada.id) ?: return false
            if (entradaExistente.estado != EstadoEntrada.BORRADOR.name) return false
            dao.actualizarEntrada(entrada.copy(estado = EstadoEntrada.ENVIADA).toEntity())
            return true
        } else {
            synchronized(entradasMemoria) {
                val idx = entradasMemoria.indexOfFirst { it.id == entrada.id }
                if (idx != -1 && entradasMemoria[idx].estado == EstadoEntrada.BORRADOR) {
                    entradasMemoria[idx] = entrada.copy(estado = EstadoEntrada.ENVIADA)
                    return true
                }
            }
            return false
        }
    }

    fun obtenerEntradasEnviadas(): Flow<List<EntradaBitacora>> {
        return if (dao != null) {
            dao.obtenerEntradasEnviadas().map { lista -> lista.map { entidad -> entidad.toModel() } }
        } else {
            flowOf(entradasMemoria.filter { it.estado == EstadoEntrada.ENVIADA })
        }
    }

    suspend fun revisarEntrada(
        entradaId: Int,
        comentarioProfesor: String
    ): Boolean {
        if (comentarioProfesor.isBlank()) return false
        if (dao != null) {
            val entradaExistente = dao.obtenerEntradaPorId(entradaId) ?: return false
            if (entradaExistente.estado != EstadoEntrada.ENVIADA.name) return false
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
                if (idx != -1 && entradasMemoria[idx].estado == EstadoEntrada.ENVIADA) {
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
