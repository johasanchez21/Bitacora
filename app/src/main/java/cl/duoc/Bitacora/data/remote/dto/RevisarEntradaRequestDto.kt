package cl.duoc.Bitacora.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RevisarEntradaRequestDto(
    @SerializedName("comentario") val comentario: String
)
