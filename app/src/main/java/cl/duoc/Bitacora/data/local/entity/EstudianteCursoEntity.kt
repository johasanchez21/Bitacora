package cl.duoc.Bitacora.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "estudiante_cursos",
    primaryKeys = ["estudianteId", "cursoId"]
)
data class EstudianteCursoEntity(
    val estudianteId: Int,
    val cursoId: Int
)
