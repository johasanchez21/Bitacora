package cl.duoc.Bitacora.ui.screens.registro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.repository.CursoRepository
import cl.duoc.Bitacora.data.repository.UsuarioRepository
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository.obtenerInstancia(),
    private val cursoRepository: CursoRepository? = null
) : ViewModel() {

    var nombre by mutableStateOf("")
        private set

    var correo by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var rol by mutableStateOf(RolUsuario.Estudiante)
        private set

    var cursosSeleccionados by mutableStateOf<List<Int>>(emptyList())
        private set

    var mensajeError by mutableStateOf("")
        private set

    var mensajeExito by mutableStateOf("")
        private set

    fun actualizarNombre(nuevo: String) {
        nombre = nuevo
        mensajeError = ""
    }

    fun actualizarCorreo(nuevo: String) {
        correo = nuevo
        mensajeError = ""
    }

    fun actualizarPassword(nuevo: String) {
        password = nuevo
        mensajeError = ""
    }

    fun actualizarRol(nuevoRol: RolUsuario) {
        rol = nuevoRol
        if (nuevoRol == RolUsuario.Profesor) {
            cursosSeleccionados = emptyList()
        }
        mensajeError = ""
    }

    fun toggleSeleccionCurso(cursoId: Int) {
        cursosSeleccionados = if (cursosSeleccionados.contains(cursoId)) {
            cursosSeleccionados - cursoId
        } else {
            cursosSeleccionados + cursoId
        }
        mensajeError = ""
    }

    fun registrar(): Boolean {
        if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
            mensajeError = "Todos los campos son obligatorios"
            return false
        }

        // Regla de negocio: El estudiante debe pertenecer a lo menos a un curso
        if (rol == RolUsuario.Estudiante && cursosSeleccionados.isEmpty()) {
            mensajeError = "Los estudiantes deben pertenecer a lo menos a un curso."
            return false
        }

        val nuevoUsuario = Usuario(
            nombre = nombre,
            correo = correo,
            password = password,
            rol = rol,
            cursoIds = cursosSeleccionados
        )

        var exitoso = false
        viewModelScope.launch {
            val resultado = usuarioRepository.registrarUsuario(nuevoUsuario)
            resultado.onSuccess {
                mensajeExito = "Usuario registrado exitosamente"
                mensajeError = ""
                exitoso = true
            }.onFailure { ex ->
                mensajeError = ex.message ?: "Error al registrar usuario"
                mensajeExito = ""
            }
        }

        return exitoso
    }
}
