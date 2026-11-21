package cl.duoc.Bitacora.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("cursoIds") val cursoIds: List<Long>? = emptyList()
)
