package cl.duoc.Bitacora.data

import cl.duoc.Bitacora.model.RolUsuario
import cl.duoc.Bitacora.model.Usuario

class UsuarioRepository {

    private val usuarios = listOf(
        Usuario(
            id = 1,
            nombre = "Sergio Weko",
            correo = "SerWeko@estudiante.cl",
            password = "123456",
            rol = RolUsuario.Estudiante
        ),
        Usuario(
            id = 2,
            nombre = "Oscar Zepela",
            correo = "OscarGey@profesor.cl",
            password = "123456",
            rol = RolUsuario.Profesor
        )
    )

    fun iniciarSesion(correo: String, password: String): Usuario?{
        return usuarios.find {
            it.correo.equals(correo, ignoreCase = true) &&
                    it.password == password
        }
    }
}