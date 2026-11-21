package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.remote.RetrofitClient
import cl.duoc.Bitacora.data.remote.dto.LoginRequestDto
import cl.duoc.Bitacora.data.remote.mapper.toDomain
import cl.duoc.Bitacora.data.remote.mapper.toDto
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario

class UsuarioRepository(
    private val apiService: BitacoraApiService = RetrofitClient.apiService
) {

    companion object {
        private val usuariosMemoria = mutableListOf(
            Usuario(
                id = 1,
                nombre = "Sergio Weko",
                correo = "SerWeko@estudiante.cl",
                password = "123456",
                rol = RolUsuario.Estudiante,
                cursoIds = listOf(1)
            ),
            Usuario(
                id = 2,
                nombre = "Oscar Zepela",
                correo = "OscarGey@profesor.cl",
                password = "123456",
                rol = RolUsuario.Profesor,
                cursoIds = listOf(1, 2)
            )
        )

        @Volatile
        private var INSTANCE: UsuarioRepository? = null

        fun obtenerInstancia(apiService: BitacoraApiService = RetrofitClient.apiService): UsuarioRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UsuarioRepository(apiService).also { INSTANCE = it }
            }
        }
    }

    suspend fun iniciarSesion(correo: String, password: String): Usuario? {
        try {
            val response = apiService.iniciarSesion(LoginRequestDto(correo.trim(), password))
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!.toDomain()
            }
        } catch (e: Exception) {
            // Fallback a memoria si el backend no responde
        }

        return synchronized(usuariosMemoria) {
            usuariosMemoria.find {
                it.correo.equals(correo.trim(), ignoreCase = true) && it.password == password
            }
        }
    }

    suspend fun registrarUsuario(usuario: Usuario): Result<Usuario> {
        try {
            val response = apiService.registrarUsuario(usuario.toDto())
            if (response.isSuccessful && response.body() != null) {
                val u = response.body()!!.toDomain()
                synchronized(usuariosMemoria) { usuariosMemoria.add(u) }
                return Result.success(u)
            } else {
                return Result.failure(Exception("Error al registrar usuario en el servidor"))
            }
        } catch (e: Exception) {
            // Fallback local
            synchronized(usuariosMemoria) {
                val nuevoId = (usuariosMemoria.maxOfOrNull { it.id } ?: 0) + 1
                val u = usuario.copy(id = nuevoId)
                usuariosMemoria.add(u)
                return Result.success(u)
            }
        }
    }
}
