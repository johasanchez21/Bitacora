package cl.duoc.Bitacora.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bitacoras")
data class BitacoraEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val asignatura: String,

    val mes: String,

    val anio: Int,

    val curso: String,

    val estado: String
)

