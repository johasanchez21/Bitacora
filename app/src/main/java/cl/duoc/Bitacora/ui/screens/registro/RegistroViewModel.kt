package cl.duoc.Bitacora.ui.screens.registro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.repository.UsuarioRepository
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import cl.duoc.Bitacora.domain.validation.LoginValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RegistroUiState {
    object Idle : RegistroUiState
    object Loading : RegistroUiState
    data class Success(val mensaje: String) : RegistroUiState
    data class Error(val mensaje: String) : RegistroUiState
}

class RegistroViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository.obtenerInstancia()
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Idle)
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    var nombre by mutableStateOf("")
        private set

    var correo by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorNombre by mutableStateOf("")
        private set

    var errorCorreo by mutableStateOf("")
        private set

    var errorPassword by mutableStateOf("")
        private set

    var rol by mutableStateOf(RolUsuario.Estudiante)
        private set

    var cursosSeleccionados by mutableStateOf<List<Int>>(emptyList())
        private set

    var mensajeError by mutableStateOf("")
        private set

    var mensajeExito by mutableStateOf("")
        private set

    var cargando by mutableStateOf(false)
        private set

    fun actualizarNombre(nuevo: String) {
        nombre = nuevo
        errorNombre = ""
        mensajeError = ""
    }

    fun actualizarCorreo(nuevo: String) {
        correo = nuevo
        errorCorreo = ""
        mensajeError = ""
    }

    fun actualizarPassword(nuevo: String) {
        password = nuevo
        errorPassword = ""
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
        var valido = true

        if (nombre.isBlank()) {
            errorNombre = "El nombre es obligatorio"
            valido = false
        } else {
            errorNombre = ""
        }

        errorCorreo = LoginValidator.validarCorreo(correo.trim())
        if (errorCorreo.isNotEmpty()) valido = false

        errorPassword = LoginValidator.validarPassword(password)
        if (errorPassword.isNotEmpty()) valido = false

        if (rol == RolUsuario.Estudiante && cursosSeleccionados.isEmpty()) {
            mensajeError = "Los estudiantes deben pertenecer a lo menos a un curso."
            valido = false
        }

        if (!valido) {
            _uiState.value = RegistroUiState.Error("Por favor corrige los campos indicados")
            return false
        }

        val nuevoUsuario = Usuario(
            nombre = nombre,
            correo = correo.trim(),
            password = password,
            rol = rol,
            cursoIds = cursosSeleccionados
        )

        cargando = true
        _uiState.value = RegistroUiState.Loading

        viewModelScope.launch {
            try {
                val resultado = usuarioRepository.registrarUsuario(nuevoUsuario)
                cargando = false
                resultado.onSuccess {
                    mensajeExito = "Usuario registrado exitosamente"
                    mensajeError = ""
                    _uiState.value = RegistroUiState.Success(mensajeExito)
                }.onFailure { ex ->
                    mensajeError = ex.message ?: "Error al registrar usuario"
                    mensajeExito = ""
                    _uiState.value = RegistroUiState.Error(mensajeError)
                }
            } catch (e: Exception) {
                cargando = false
                mensajeError = "Error al conectar con el servidor"
                _uiState.value = RegistroUiState.Error(mensajeError)
            }
        }

        return true
    }
}
