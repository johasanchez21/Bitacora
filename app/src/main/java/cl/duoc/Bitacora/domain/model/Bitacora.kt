package cl.duoc.Bitacora.domain.model

data class Bitacora(
    val id: Int,
    val asignatura: String,
    val mes: String,
    val anio: Int,
    val curso: String,
    val estado: EstadoBitacora
)
