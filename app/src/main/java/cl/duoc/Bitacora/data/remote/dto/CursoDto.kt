package cl.duoc.Bitacora.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CursoDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("anio") val anio: Int,
    @SerializedName("semestre") val semestre: Int
)
