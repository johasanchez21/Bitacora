package cl.duoc.Bitacora.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entradas_bitacora")
data class EntradaBitacoraEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val bitacoraId: Int,

    val estudianteId: Int,

    val fecha: String,

    val actividad: String,

    val aprendizaje: String,

    val dudas: String,

    val estado: String,

    val comentarioProfesor: String = "",

    val fotoUri: String? = null
)

