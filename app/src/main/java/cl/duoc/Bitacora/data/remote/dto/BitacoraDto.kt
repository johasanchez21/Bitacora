package cl.duoc.Bitacora.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BitacoraDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("asignatura") val asignatura: String,
    @SerializedName("mes") val mes: String,
    @SerializedName("anio") val anio: Int,
    @SerializedName("cursoId") val cursoId: Long? = null,
    @SerializedName("curso") val curso: String,
    @SerializedName("profesorId") val profesorId: Long? = null,
    @SerializedName("estado") val estado: String
)
