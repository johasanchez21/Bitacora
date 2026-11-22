package cl.duoc.Bitacora.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.Bitacora.data.local.entity.BitacoraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarBitacora(bitacora: BitacoraEntity): Long

    @Query("SELECT * FROM bitacoras ORDER BY id DESC")
    fun obtenerTodasLasBitacoras(): Flow<List<BitacoraEntity>>

    @Query("SELECT * FROM bitacoras WHERE curso = :curso AND estado = 'PUBLICADA' ORDER BY id DESC")
    fun obtenerBitacorasPublicadas(curso: String): Flow<List<BitacoraEntity>>

    @Query("SELECT * FROM bitacoras WHERE cursoId = :cursoId ORDER BY id DESC")
    fun obtenerBitacorasPorCursoId(cursoId: Int): Flow<List<BitacoraEntity>>

    @Query("SELECT * FROM bitacoras WHERE id = :id LIMIT 1")
    suspend fun obtenerBitacoraPorId(id: Int): BitacoraEntity?

    @Query("UPDATE bitacoras SET estado = :nuevoEstado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, nuevoEstado: String): Int

    @Update
    suspend fun actualizarBitacora(bitacora: BitacoraEntity)
}
