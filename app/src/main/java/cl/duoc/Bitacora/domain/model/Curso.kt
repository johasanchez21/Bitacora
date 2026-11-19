package cl.duoc.Bitacora.domain.model

data class Curso(
    val id: Int = 0,
    val nombre: String,
    val codigo: String,
    val anio: Int,
    val semestre: Int = 1
)
