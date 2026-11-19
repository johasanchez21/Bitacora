package cl.duoc.Bitacora.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.Bitacora.data.local.entity.EstudianteCursoEntity
import cl.duoc.Bitacora.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    @Insert
    suspend fun insertarEstudianteCurso(relacion: EstudianteCursoEntity)

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Int): UsuarioEntity?

    @Query("SELECT cursoId FROM estudiante_cursos WHERE estudianteId = :estudianteId")
    suspend fun obtenerCursosDeEstudiante(estudianteId: Int): List<Int>
}
