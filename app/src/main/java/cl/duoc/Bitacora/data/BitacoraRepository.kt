package cl.duoc.Bitacora.data

import cl.duoc.Bitacora.data.local.BitacoraDao
import cl.duoc.Bitacora.model.Bitacora
import cl.duoc.Bitacora.model.EstadoBitacora
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BitacoraRepository(
    private val bitacoraDao: BitacoraDao
) {

    suspend fun guardarBitacora(
        bitacora: Bitacora
    ): Long {
        return bitacoraDao.insertarBitacora(
            bitacora.toEntity()
        )
    }

    fun obtenerTodasLasBitacorasRoom(): Flow<List<Bitacora>> {
        return bitacoraDao.obtenerTodasLasBitacoras()
            .map { entidades ->
                entidades.map { it.toModel() }
            }
    }

    fun obtenerBitacorasPublicadasRoom(
        curso: String
    ): Flow<List<Bitacora>> {
        return bitacoraDao.obtenerBitacorasPublicadas(curso)
            .map { entidades ->
                entidades.map { it.toModel() }
            }
    }

    suspend fun obtenerBitacoraPorIdRoom(
        id: Int
    ): Bitacora? {
        return bitacoraDao.obtenerBitacoraPorId(id)
            ?.toModel()
    }

    suspend fun publicarBitacora(
        id: Int
    ): Boolean {

        val bitacora = bitacoraDao.obtenerBitacoraPorId(id)
            ?: return false

        if (bitacora.estado != EstadoBitacora.BORRADOR.name) {
            return false
        }

        return bitacoraDao.actualizarEstado(
            id = id,
            nuevoEstado = EstadoBitacora.PUBLICADA.name
        ) > 0
    }
}
