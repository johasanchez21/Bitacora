package cl.duoc.Bitacora.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.Bitacora.data.local.entity.CursoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CursoDao {

    @Insert
    suspend fun insertarCurso(curso: CursoEntity): Long

    @Query("SELECT * FROM cursos ORDER BY anio DESC, semestre ASC")
    fun obtenerTodosLosCursos(): Flow<List<CursoEntity>>

    @Query("SELECT * FROM cursos WHERE anio = :anio")
    suspend fun obtenerCursosPorAnio(anio: Int): List<CursoEntity>

    @Query("SELECT COUNT(*) FROM cursos WHERE anio = :anio")
    suspend fun contarCursosPorAnio(anio: Int): Int

    @Query("SELECT * FROM cursos WHERE id = :id LIMIT 1")
    suspend fun obtenerCursoPorId(id: Int): CursoEntity?
}
