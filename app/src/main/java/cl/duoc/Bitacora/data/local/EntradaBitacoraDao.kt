package cl.duoc.Bitacora.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntradaBitacoraDao {

    @Insert
    suspend fun insertarEntrada(
        entrada: EntradaBitacoraEntity
    ): Long

    @Query(
        "SELECT * FROM entradas_bitacora " +
                "WHERE estudianteId = :estudianteId " +
                "ORDER BY id DESC"
    )
    fun obtenerEntradasPorEstudiante(
        estudianteId: Int
    ): Flow<List<EntradaBitacoraEntity>>

    @Query(
        "SELECT * FROM entradas_bitacora WHERE id = :id LIMIT 1"
    )
    suspend fun obtenerEntradaPorId(
        id: Int
    ): EntradaBitacoraEntity?

    @Update
    suspend fun actualizarEntrada(
        entrada: EntradaBitacoraEntity
    )


    @Query(
        """
    SELECT * FROM entradas_bitacora
    WHERE estado = 'ENVIADA'
    ORDER BY id DESC
    """
    )
    fun obtenerEntradasEnviadas(): Flow<List<EntradaBitacoraEntity>>

}

