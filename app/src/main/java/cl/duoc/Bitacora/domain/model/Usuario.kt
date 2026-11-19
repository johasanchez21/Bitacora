package cl.duoc.Bitacora.domain.model

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val password: String,
    val rol: RolUsuario,
    val cursoIds: List<Int> = emptyList()
)
