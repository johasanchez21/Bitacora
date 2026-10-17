package cl.duoc.Bitacora.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.Bitacora.validation.EntradaBitacoraValidator

class EntradaBitacoraViewModel : ViewModel() {

    var fecha by mutableStateOf("")
        private set

    var actividad by mutableStateOf("")
        private set

    var aprendizaje by mutableStateOf("")
        private set

    var dudas by mutableStateOf("")
        private set

    var errorFecha by mutableStateOf("")
        private set

    var errorActividad by mutableStateOf("")
        private set

    var errorAprendizaje by mutableStateOf("")
        private set

    var errorDudas by mutableStateOf("")
        private set

    fun actualizarFecha(nuevaFecha: String) {
        fecha = nuevaFecha
        errorFecha = ""
    }

    fun actualizarActividad(nuevaActividad: String) {
        actividad = nuevaActividad
        errorActividad = ""
    }

    fun actualizarAprendizaje(nuevoAprendizaje: String) {
        aprendizaje = nuevoAprendizaje
        errorAprendizaje = ""
    }

    fun actualizarDudas(nuevasDudas: String) {
        dudas = nuevasDudas
        errorDudas = ""
    }

    fun validarFormulario(): Boolean {

        errorFecha =
            EntradaBitacoraValidator.validarFecha(fecha)

        errorActividad =
            EntradaBitacoraValidator.validarActividad(actividad)

        errorAprendizaje =
            EntradaBitacoraValidator.validarAprendizaje(aprendizaje)

        errorDudas =
            EntradaBitacoraValidator.validarDudas(dudas)

        return errorFecha.isEmpty() &&
                errorActividad.isEmpty() &&
                errorAprendizaje.isEmpty() &&
                errorDudas.isEmpty()
    }
}

