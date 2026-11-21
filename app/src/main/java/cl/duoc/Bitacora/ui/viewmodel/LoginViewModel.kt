package cl.duoc.Bitacora.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.repository.UsuarioRepository
import cl.duoc.Bitacora.domain.model.Usuario
import cl.duoc.Bitacora.domain.validation.LoginValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val usuario: Usuario, val mensaje: String) : LoginUiState
    data class Error(val mensaje: String) : LoginUiState
}

class LoginViewModel(
    private val repository: UsuarioRepository = UsuarioRepository.obtenerInstancia()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    var correo by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorCorreo by mutableStateOf("")
        private set

    var errorPassword by mutableStateOf("")
        private set

    var errorLogin by mutableStateOf("")
        private set

    var mensajeExito by mutableStateOf("")
        private set

    var cargando by mutableStateOf(false)
        private set

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    fun actualiazrCorreo(nuevoCorreo: String) {
        correo = nuevoCorreo
        errorCorreo = ""
        errorLogin = ""
        mensajeExito = ""
        _uiState.value = LoginUiState.Idle
    }

    fun actualizarPassword(nuevaPass: String) {
        password = nuevaPass
        errorPassword = ""
        errorLogin = ""
        mensajeExito = ""
        _uiState.value = LoginUiState.Idle
    }

    fun iniciarSesion(onExito: (Usuario) -> Unit = {}): Boolean {
        errorCorreo = LoginValidator.validarCorreo(correo.trim())
        errorPassword = LoginValidator.validarPassword(password)

        if (errorCorreo.isNotEmpty() || errorPassword.isNotEmpty()) {
            _uiState.value = LoginUiState.Error("Por favor corrige los campos indicados")
            return false
        }

        cargando = true
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val usuario = repository.iniciarSesion(correo.trim(), password)
                cargando = false
                if (usuario != null) {
                    usuarioActual = usuario
                    errorLogin = ""
                    mensajeExito = "¡Sesión iniciada correctamente como ${usuario.nombre}!"
                    _uiState.value = LoginUiState.Success(usuario, mensajeExito)
                    onExito(usuario)
                } else {
                    usuarioActual = null
                    mensajeExito = ""
                    errorLogin = "Correo o contraseña incorrectos"
                    _uiState.value = LoginUiState.Error(errorLogin)
                }
            } catch (e: Exception) {
                cargando = false
                errorLogin = "Error al conectar con el servidor"
                _uiState.value = LoginUiState.Error(errorLogin)
            }
        }

        return true
    }
}
