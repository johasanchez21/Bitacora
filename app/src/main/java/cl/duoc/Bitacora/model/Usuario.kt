package cl.duoc.Bitacora.model

data class Usuario (
    val id: Int,
    val nombre: String,
    val correo: String,
    val password: String,
    val rol: RolUsuario
)