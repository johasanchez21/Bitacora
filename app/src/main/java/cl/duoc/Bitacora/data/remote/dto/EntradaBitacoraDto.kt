package cl.duoc.Bitacora.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EntradaBitacoraDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("bitacoraId") val bitacoraId: Long,
    @SerializedName("estudianteId") val estudianteId: Long,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("actividad") val actividad: String,
    @SerializedName("aprendizaje") val aprendizaje: String,
    @SerializedName("dudas") val dudas: String? = "",
    @SerializedName("estado") val estado: String,
    @SerializedName("comentarioProfesor") val comentarioProfesor: String? = "",
    @SerializedName("fotoUri") val fotoUri: String? = null
)
