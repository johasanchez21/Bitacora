package cl.duoc.Bitacora.domain.model

data class EntradaBitacora(
    val id: Int = 0,
    val bitacoraId: Int,
    val estudianteId: Int,
    val fecha: String,
    val actividad: String,
    val aprendizaje: String,
    val dudas: String = "",
    val estado: EstadoEntrada = EstadoEntrada.BORRADOR,
    val comentarioProfesor: String = "",
    val fotoUri: String? = null,
    val fotosUris: List<String> = emptyList()
) {
    fun obtenerListaFotos(): List<String> {
        if (fotosUris.isNotEmpty()) return fotosUris
        if (!fotoUri.isNullOrBlank()) {
            return fotoUri.split(";").filter { it.isNotBlank() }
        }
        return emptyList()
    }
}
