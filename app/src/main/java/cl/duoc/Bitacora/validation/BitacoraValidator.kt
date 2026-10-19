package cl.duoc.Bitacora.validation

object BitacoraValidator {

    private val mesesValidos = listOf(
        "enero",
        "febrero",
        "marzo",
        "abril",
        "mayo",
        "junio",
        "julio",
        "agosto",
        "septiembre",
        "octubre",
        "noviembre",
        "diciembre"
    )

    fun validarAsignatura(asignatura: String): String? {
        return when {
            asignatura.isBlank() ->
                "La asignatura es obligatoria"

            asignatura.trim().length < 3 ->
                "La asignatura debe tener al menos 3 caracteres"

            else -> null
        }
    }

    fun validarMes(mes: String): String? {
        return when {
            mes.isBlank() ->
                "El mes es obligatorio"

            mes.trim().lowercase() !in mesesValidos ->
                "Ingresa un mes válido"

            else -> null
        }
    }

    fun validarAnio(anio: String): String? {
        val numero = anio.toIntOrNull()

        return when {
            anio.isBlank() ->
                "El año es obligatorio"

            numero == null ->
                "El año debe ser numérico"

            numero !in 2020..2100 ->
                "El año debe estar entre 2020 y 2100"

            else -> null
        }
    }

    fun validarCurso(curso: String): String? {
        return when {
            curso.isBlank() ->
                "El curso es obligatorio"

            curso.trim().length < 3 ->
                "El curso debe tener al menos 3 caracteres"

            else -> null
        }
    }
}
