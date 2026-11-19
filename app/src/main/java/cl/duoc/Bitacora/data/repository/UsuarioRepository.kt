package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.local.dao.UsuarioDao
import cl.duoc.Bitacora.data.local.entity.EstudianteCursoEntity
import cl.duoc.Bitacora.data.local.entity.UsuarioEntity
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario

class UsuarioRepository(
    private val usuarioDao: UsuarioDao? = null
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
                rol = RolUsuario.Profesor
            )
        )

        @Volatile
        private var INSTANCE: UsuarioRepository? = null

        fun obtenerInstancia(usuarioDao: UsuarioDao? = null): UsuarioRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UsuarioRepository(usuarioDao).also { INSTANCE = it }
            }
        }
    }

    /**
     * Regla de Negocio 2: Los estudiantes deben pertenecer a lo menos a un curso.
     */
    suspend fun registrarUsuario(usuario: Usuario): Result<Long> {
        if (usuario.rol == RolUsuario.Estudiante && usuario.cursoIds.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("El estudiante debe pertenecer a lo menos a un curso.")
            )
        }

        if (usuarioDao != null) {
            val entity = UsuarioEntity(
                id = usuario.id,
                nombre = usuario.nombre,
                correo = usuario.correo,
                password = usuario.password,
                rol = usuario.rol.name
            )
            val id = usuarioDao.insertarUsuario(entity)
            if (usuario.rol == RolUsuario.Estudiante) {
                usuario.cursoIds.forEach { cursoId ->
                    usuarioDao.insertarEstudianteCurso(
                        EstudianteCursoEntity(
                            estudianteId = id.toInt(),
                            cursoId = cursoId
                        )
                    )
                }
            }
            val usuarioConId = usuario.copy(id = id.toInt())
            synchronized(usuariosMemoria) {
                usuariosMemoria.add(usuarioConId)
            }
            return Result.success(id)
        } else {
            synchronized(usuariosMemoria) {
                val nuevoId = (usuariosMemoria.maxOfOrNull { it.id } ?: 0) + 1
                val usuarioGuardado = usuario.copy(id = nuevoId)
                usuariosMemoria.add(usuarioGuardado)
                return Result.success(nuevoId.toLong())
            }
        }
    }

    suspend fun iniciarSesion(correo: String, password: String): Usuario? {
        if (usuarioDao != null) {
            val entity = usuarioDao.obtenerUsuarioPorCorreo(correo)
            if (entity != null && entity.password == password) {
                val rol = RolUsuario.valueOf(entity.rol)
                val cursos = if (rol == RolUsuario.Estudiante) {
                    usuarioDao.obtenerCursosDeEstudiante(entity.id)
                } else emptyList()
                return Usuario(
                    id = entity.id,
                    nombre = entity.nombre,
                    correo = entity.correo,
                    password = entity.password,
                    rol = rol,
                    cursoIds = cursos
                )
            }
        }

        return synchronized(usuariosMemoria) {
            usuariosMemoria.find {
                it.correo.equals(correo, ignoreCase = true) &&
                        it.password == password
            }
        }
    }
}
