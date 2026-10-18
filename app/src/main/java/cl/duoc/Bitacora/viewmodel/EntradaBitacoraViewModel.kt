package cl.duoc.Bitacora.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.Bitacora.data.EntradaBitacoraRepository
import cl.duoc.Bitacora.model.EntradaBitacora
import cl.duoc.Bitacora.model.EstadoEntrada
import cl.duoc.Bitacora.validation.EntradaBitacoraValidator
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.local.BitacoraDatabase
import kotlinx.coroutines.launch

class EntradaBitacoraViewModel(
    application: Application
) : AndroidViewModel(application) {

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

    var borradorId by mutableStateOf<Int?>(null)
        private set

    private val dao = BitacoraDatabase
        .obtenerInstancia(application)
        .entradaBitacoraDao()

    private val repository = EntradaBitacoraRepository(dao)

    var mensajeResultado by mutableStateOf("")
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

    fun cargarBorrador(
        entradaId: Int,
        estudianteId: Int,
        bitacoraId: Int
    ) {
        viewModelScope.launch {
            try {
                val entrada = repository.obtenerEntradaPorId(entradaId)

                if (
                    entrada == null ||
                    entrada.estudianteId != estudianteId ||
                    entrada.bitacoraId != bitacoraId ||
                    entrada.estado != EstadoEntrada.BORRADOR
                ) {
                    mensajeResultado = "No se encontró un borrador válido"
                    return@launch
                }

                borradorId = entrada.id

                fecha = entrada.fecha
                actividad = entrada.actividad
                aprendizaje = entrada.aprendizaje
                dudas = entrada.dudas

                errorFecha = ""
                errorActividad = ""
                errorAprendizaje = ""
                errorDudas = ""

                mensajeResultado = ""

            } catch (e: Exception) {
                mensajeResultado = "Error al cargar el borrador"
            }
        }
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


    fun guardarBorrador(
        estudianteId: Int,
        bitacoraId: Int
    ): Boolean {

        if (estudianteId <= 0 || bitacoraId <= 0) {
            mensajeResultado =
                "No se pudo identificar al estudiante o la bitácora"
            return false
        }

        val entrada = EntradaBitacora(
            id = borradorId ?: 0,
            bitacoraId = bitacoraId,
            estudianteId = estudianteId,
            fecha = fecha,
            actividad = actividad,
            aprendizaje = aprendizaje,
            dudas = dudas,
            estado = EstadoEntrada.BORRADOR
        )

        mensajeResultado = "Guardando borrador..."

        viewModelScope.launch {
            try {
                if (borradorId == null) {

                    val nuevoId = repository.guardarEntrada(entrada)

                    borradorId = nuevoId.toInt()

                    mensajeResultado =
                        "Borrador guardado correctamente"

                } else {

                    val actualizado = repository.actualizarBorrador(entrada)

                    mensajeResultado = if (actualizado) {
                        "Borrador actualizado correctamente"
                    } else {
                        "No se pudo actualizar el borrador"
                    }
                }

            } catch (e: Exception) {
                mensajeResultado = "Error al guardar el borrador"
            }
        }

        return true
    }



    fun enviarEntrada(
        estudianteId: Int,
        bitacoraId: Int
    ): Boolean {

        if (!validarFormulario()) {
            mensajeResultado = "Debes corregir los errores del formulario"
            return false
        }

        if (estudianteId <= 0 || bitacoraId <= 0) {
            mensajeResultado =
                "No se pudo identificar al estudiante o la bitácora"
            return false
        }

        val entrada = EntradaBitacora(
            id = borradorId ?: 0,
            bitacoraId = bitacoraId,
            estudianteId = estudianteId,
            fecha = fecha,
            actividad = actividad,
            aprendizaje = aprendizaje,
            dudas = dudas,
            estado = EstadoEntrada.ENVIADA
        )

        mensajeResultado = "Enviando entrada..."

        viewModelScope.launch {
            try {

                if (borradorId == null) {

                    repository.guardarEntrada(entrada)

                    mensajeResultado = "Entrada enviada correctamente"

                } else {

                    val enviada = repository.enviarBorrador(entrada)

                    if (enviada) {
                        borradorId = null
                        mensajeResultado = "Entrada enviada correctamente"
                    } else {
                        mensajeResultado = "No se pudo enviar el borrador"
                    }
                }

            } catch (e: Exception) {
                mensajeResultado = "Error al enviar la entrada"
            }
        }

        return true
    }

    fun limpiarFormulario() {

        borradorId = null

        fecha = ""
        actividad = ""
        aprendizaje = ""
        dudas = ""

        errorFecha = ""
        errorActividad = ""
        errorAprendizaje = ""
        errorDudas = ""

        mensajeResultado = ""
    }

}

