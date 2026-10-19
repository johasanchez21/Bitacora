package cl.duoc.Bitacora.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {

    @Insert
    suspend fun insertarBitacora(
        bitacora: BitacoraEntity
    ): Long

    @Query(
        """
        SELECT * FROM bitacoras
        ORDER BY id DESC
        """
    )
    fun obtenerTodasLasBitacoras(): Flow<List<BitacoraEntity>>

    @Query(
        """
        SELECT * FROM bitacoras
        WHERE curso = :curso
        AND estado = 'PUBLICADA'
        ORDER BY id DESC
        """
    )
    fun obtenerBitacorasPublicadas(
        curso: String
    ): Flow<List<BitacoraEntity>>

    @Query(
        """
        SELECT * FROM bitacoras
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun obtenerBitacoraPorId(
        id: Int
    ): BitacoraEntity?

    @Query(
        """
        UPDATE bitacoras
        SET estado = :nuevoEstado
        WHERE id = :id
        """
    )
    suspend fun actualizarEstado(
        id: Int,
        nuevoEstado: String
    ): Int

    @Query(
        """
    UPDATE bitacoras
    SET id = :nuevoId
    WHERE id = :idActual
    """
    )
    suspend fun cambiarIdBitacora(
        idActual: Int,
        nuevoId: Int
    ): Int

    @Query(
        """
    SELECT COUNT(*) FROM bitacoras
    WHERE id = :id
    """
    )
    suspend fun existeBitacoraConId(id: Int): Int

}

