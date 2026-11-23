package cl.duoc.Bitacora.domain.validation

object EntradaBitacoraValidator {

    private val LETRA_REGEX = Regex(".*[a-zA-ZáéíóúÁÉÍÓÚñÑ].*")

    fun validarFecha(fecha: String): String {
        return if (fecha.isBlank()) {
            "Debes seleccionar una fecha"
        } else {
            ""
        }
    }

    fun validarActividad(actividad: String): String {
        return when {
            actividad.isBlank() ->
                "Debes ingresar una actividad"

            actividad.trim().length < 10 ->
                "La actividad debe tener al menos 10 caracteres"

            actividad.length > 500 ->
                "La actividad no puede superar los 500 caracteres"

            !actividad.matches(LETRA_REGEX) ->
                "La actividad debe contener al menos una letra"

            else -> ""
        }
    }

    fun validarAprendizaje(aprendizaje: String): String {
        return when {
            aprendizaje.isBlank() ->
                "Debes describir qué aprendiste"

            aprendizaje.trim().length < 10 ->
                "El aprendizaje debe tener al menos 10 caracteres"

            aprendizaje.length > 500 ->
                "El aprendizaje no puede superar los 500 caracteres"

            !aprendizaje.matches(LETRA_REGEX) ->
                "El aprendizaje debe contener al menos una letra"

            else -> ""
        }
    }

    fun validarDudas(dudas: String): String {
        if (dudas.isBlank()) return ""

        return when {
            dudas.trim().length < 5 ->
                "Las dudas deben tener al menos 5 caracteres"

            dudas.length > 300 ->
                "Las dudas no pueden superar los 300 caracteres"

            !dudas.matches(LETRA_REGEX) ->
                "Las dudas deben contener al menos una letra"

            else -> ""
        }
    }
}
