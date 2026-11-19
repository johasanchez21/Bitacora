package cl.duoc.Bitacora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bitacoras")
data class BitacoraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val asignatura: String,
    val mes: String,
    val anio: Int,
    val cursoId: Int = 0,
    val curso: String,
    val profesorId: Int = 0,
    val estado: String
)
