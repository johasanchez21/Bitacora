package cl.duoc.Bitacora.validation

object LoginValidator {

    fun validarCorreo(correo: String): String {
        if (correo.isBlank()) {
            return "El correo es obligatorio"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            return "Ingresa un correo válido"
        }

        return ""
    }

    fun validarPassword(password: String): String {
        if (password.isBlank()) {
            return "La contraseña es obligatoria"
        }

        if (password.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres"
        }

        return ""
    }
}
