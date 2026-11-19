package cl.duoc.Bitacora.domain.model

data class Bitacora(
    val id: Int = 0,
    val asignatura: String,
    val mes: String,
    val anio: Int,
    val cursoId: Int = 0,
    val curso: String,
    val profesorId: Int = 0,
    val estado: EstadoBitacora = EstadoBitacora.BORRADOR
)
