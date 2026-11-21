package cl.duoc.Bitacora.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.local.BitacoraDatabase
import cl.duoc.Bitacora.data.repository.EntradaBitacoraRepository
import cl.duoc.Bitacora.data.repository.FotoRepository
import cl.duoc.Bitacora.domain.model.EntradaBitacora
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import cl.duoc.Bitacora.domain.validation.EntradaBitacoraValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EntradaUiState {
    object Idle : EntradaUiState
    object Loading : EntradaUiState
    data class Success(val mensaje: String) : EntradaUiState
    data class Error(val mensaje: String) : EntradaUiState
}

class EntradaBitacoraViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<EntradaUiState>(EntradaUiState.Idle)
    val uiState: StateFlow<EntradaUiState> = _uiState.asStateFlow()

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
    private val fotoRepository = FotoRepository(application)

    var mensajeResultado by mutableStateOf("")
        private set

    var fotoUri by mutableStateOf<String?>(null)
        private set

    var guardandoFoto by mutableStateOf(false)
        private set

    var errorFoto by mutableStateOf<String?>(null)
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
                    _uiState.value = EntradaUiState.Error(mensajeResultado)
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
                fotoUri = entrada.fotoUri
                _uiState.value = EntradaUiState.Idle

            } catch (e: Exception) {
                mensajeResultado = "Error al cargar el borrador"
                _uiState.value = EntradaUiState.Error(mensajeResultado)
            }
        }
    }

    fun validarFormulario(): Boolean {
        errorFecha = EntradaBitacoraValidator.validarFecha(fecha)
        errorActividad = EntradaBitacoraValidator.validarActividad(actividad)
        errorAprendizaje = EntradaBitacoraValidator.validarAprendizaje(aprendizaje)
        errorDudas = EntradaBitacoraValidator.validarDudas(dudas)

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
            mensajeResultado = "No se pudo identificar al estudiante o la bitácora"
            _uiState.value = EntradaUiState.Error(mensajeResultado)
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
            estado = EstadoEntrada.BORRADOR,
            fotoUri = fotoUri
        )

        mensajeResultado = "Guardando borrador..."
        _uiState.value = EntradaUiState.Loading

        viewModelScope.launch {
            try {
                if (borradorId == null) {
                    val nuevoId = repository.guardarEntrada(entrada)
                    borradorId = nuevoId.toInt()
                    mensajeResultado = "Borrador guardado correctamente"
                    _uiState.value = EntradaUiState.Success(mensajeResultado)
                } else {
                    val actualizado = repository.actualizarBorrador(entrada)
                    if (actualizado) {
                        mensajeResultado = "Borrador actualizado correctamente"
                        _uiState.value = EntradaUiState.Success(mensajeResultado)
                    } else {
                        mensajeResultado = "No se pudo actualizar el borrador"
                        _uiState.value = EntradaUiState.Error(mensajeResultado)
                    }
                }
            } catch (e: Exception) {
                mensajeResultado = "Error al guardar el borrador"
                _uiState.value = EntradaUiState.Error(mensajeResultado)
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
            _uiState.value = EntradaUiState.Error(mensajeResultado)
            return false
        }

        if (estudianteId <= 0 || bitacoraId <= 0) {
            mensajeResultado = "No se pudo identificar al estudiante o la bitácora"
            _uiState.value = EntradaUiState.Error(mensajeResultado)
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
            estado = EstadoEntrada.ENVIADA,
            fotoUri = fotoUri
        )

        mensajeResultado = "Enviando entrada..."
        _uiState.value = EntradaUiState.Loading

        viewModelScope.launch {
            try {
                if (borradorId == null) {
                    repository.guardarEntrada(entrada)
                    mensajeResultado = "Entrada enviada correctamente"
                    _uiState.value = EntradaUiState.Success(mensajeResultado)
                } else {
                    val enviada = repository.enviarBorrador(entrada)
                    if (enviada) {
                        borradorId = null
                        mensajeResultado = "Entrada enviada correctamente"
                        _uiState.value = EntradaUiState.Success(mensajeResultado)
                    } else {
                        mensajeResultado = "No se pudo enviar el borrador"
                        _uiState.value = EntradaUiState.Error(mensajeResultado)
                    }
                }
            } catch (e: Exception) {
                mensajeResultado = "Error al enviar la entrada"
                _uiState.value = EntradaUiState.Error(mensajeResultado)
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
        fotoUri = null
        errorFoto = null
        _uiState.value = EntradaUiState.Idle
    }

    fun guardarFotografia(uriSeleccionada: Uri) {
        if (guardandoFoto) return

        viewModelScope.launch {
            guardandoFoto = true
            errorFoto = null

            try {
                val rutaGuardada = fotoRepository.guardarFoto(uriSeleccionada)
                if (rutaGuardada != null) {
                    fotoUri = rutaGuardada
                } else {
                    errorFoto = "No se pudo guardar la fotografía"
                }
            } catch (e: Exception) {
                errorFoto = "Error al guardar la fotografía"
            } finally {
                guardandoFoto = false
            }
        }
    }
}
