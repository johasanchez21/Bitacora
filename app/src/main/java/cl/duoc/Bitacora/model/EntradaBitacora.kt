package cl.duoc.Bitacora.model

data class EntradaBitacora(
    val id: Int,
    val bitacoraId: Int,
    val estudianteId: Int,
    val fecha: String,
    val actividad: String,
    val aprendizaje: String,
    val dudas: String,
    val estado: EstadoEntrada,
    val comentarioProfesor: String = ""
)

