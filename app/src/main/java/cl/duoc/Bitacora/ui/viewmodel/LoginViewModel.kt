package cl.duoc.Bitacora.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.repository.UsuarioRepository
import cl.duoc.Bitacora.domain.model.Usuario
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UsuarioRepository = UsuarioRepository.obtenerInstancia()
) : ViewModel() {

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

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    fun actualiazrCorreo(nuevoCorreo: String) {
        correo = nuevoCorreo
        errorCorreo = ""
        errorLogin = ""
        mensajeExito = ""
    }

    fun actualizarPassword(nuevaPass: String) {
        password = nuevaPass
        errorPassword = ""
        errorLogin = ""
        mensajeExito = ""
    }

    fun iniciarSesion(onExito: (Usuario) -> Unit = {}): Boolean {
        var valido = true

        if (correo.isBlank()) {
            errorCorreo = "El correo es requerido"
            valido = false
        }

        if (password.isBlank()) {
            errorPassword = "La contraseña es requerida"
            valido = false
        }

        if (!valido) return false

        viewModelScope.launch {
            val usuario = repository.iniciarSesion(correo, password)
            if (usuario != null) {
                usuarioActual = usuario
                errorLogin = ""
                mensajeExito = "¡Sesión iniciada correctamente como ${usuario.nombre}!"
                onExito(usuario)
            } else {
                usuarioActual = null
                mensajeExito = ""
                errorLogin = "Credenciales incorrectas"
            }
        }

        return true
    }
}
