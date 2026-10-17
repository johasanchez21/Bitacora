package cl.duoc.Bitacora.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import cl.duoc.Bitacora.data.UsuarioRepository
import cl.duoc.Bitacora.model.Usuario
import cl.duoc.Bitacora.validation.LoginValidator

class LoginViewModel : ViewModel() {
    var correo by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var errorCorreo by mutableStateOf("")
        private set
    var errorPassword by mutableStateOf("")
        private set

    private val usuarioRepository = UsuarioRepository()

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set
    var errorLogin by mutableStateOf("")
        private set

    fun actualiazrCorreo(nuevoCorreo: String){
        correo = nuevoCorreo
        errorCorreo = ""
    }

    fun actualizarPassword(nuevaPassword : String){
        password = nuevaPassword
        errorPassword = ""
    }


    fun iniciarSesion(): Boolean {

        errorCorreo = LoginValidator.validarCorreo(correo.trim())
        errorPassword = LoginValidator.validarPassword(password)

        if (errorCorreo.isNotEmpty() || errorPassword.isNotEmpty()) {
            usuarioActual = null
            errorLogin = ""
            return false
        }

        val usuario = usuarioRepository.iniciarSesion(
            correo.trim(),
            password
        )

        if (usuario != null) {
            usuarioActual = usuario
            errorLogin = ""
            return true
        } else {
            usuarioActual = null
            errorLogin = "Correo o contraseña incorrectos"
            return false
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        correo = ""
        password = ""
        errorCorreo = ""
        errorPassword = ""
        errorLogin = ""
    }

}