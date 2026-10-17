package cl.duoc.Bitacora.validation

object EntradaBitacoraValidator {

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

            else -> ""
        }
    }

    fun validarAprendizaje(aprendizaje: String): String {
        return when {
            aprendizaje.isBlank() ->
                "Debes describir qué aprendiste"

            aprendizaje.trim().length < 10 ->
                "El aprendizaje debe tener al menos 10 caracteres"

            else -> ""
        }
    }

    fun validarDudas(dudas: String): String {
        return if (dudas.length > 500) {
            "Las dudas no pueden superar los 500 caracteres"
        } else {
            ""
        }
    }
}
