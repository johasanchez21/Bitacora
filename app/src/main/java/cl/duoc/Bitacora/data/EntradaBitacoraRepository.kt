package cl.duoc.Bitacora.data

import cl.duoc.Bitacora.data.local.EntradaBitacoraDao
import cl.duoc.Bitacora.model.EntradaBitacora
import cl.duoc.Bitacora.model.EstadoEntrada
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EntradaBitacoraRepository(
    private val dao: EntradaBitacoraDao
) {

    suspend fun guardarEntrada(
        entrada: EntradaBitacora
    ): Long {
        return dao.insertarEntrada(
            entrada.toEntity()
        )
    }

    fun obtenerEntradasPorEstudiante(
        estudianteId: Int
    ): Flow<List<EntradaBitacora>> {

        return dao.obtenerEntradasPorEstudiante(estudianteId)
            .map { lista ->
                lista.map { entidad ->
                    entidad.toModel()
                }
            }
    }

    suspend fun obtenerEntradaPorId(
        id: Int
    ): EntradaBitacora? {

        return dao.obtenerEntradaPorId(id)?.toModel()
    }

    suspend fun actualizarEstado(
        id: Int,
        nuevoEstado: EstadoEntrada
    ): Boolean {

        val entrada = dao.obtenerEntradaPorId(id)
            ?: return false

        dao.actualizarEntrada(
            entrada.copy(
                estado = nuevoEstado.name
            )
        )

        return true
    }

    suspend fun actualizarBorrador(
        entrada: EntradaBitacora
    ): Boolean {

        val entradaExistente = dao.obtenerEntradaPorId(entrada.id)
            ?: return false

        if (entradaExistente.estado != EstadoEntrada.BORRADOR.name) {
            return false
        }

        if (entradaExistente.estudianteId != entrada.estudianteId ||
            entradaExistente.bitacoraId != entrada.bitacoraId) {
            return false
        }

        dao.actualizarEntrada(
            entrada.toEntity()
        )

        return true
    }

    suspend fun enviarBorrador(
        entrada: EntradaBitacora
    ): Boolean {

        val entradaExistente = dao.obtenerEntradaPorId(entrada.id)
            ?: return false

        if (entradaExistente.estado != EstadoEntrada.BORRADOR.name) {
            return false
        }

        if (
            entradaExistente.estudianteId != entrada.estudianteId ||
            entradaExistente.bitacoraId != entrada.bitacoraId
        ) {
            return false
        }

        dao.actualizarEntrada(
            entrada.copy(
                estado = EstadoEntrada.ENVIADA
            ).toEntity()
        )

        return true
    }

}

