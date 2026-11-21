package cl.duoc.Bitacora.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.local.BitacoraDatabase
import cl.duoc.Bitacora.data.repository.BitacoraRepository
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.EstadoBitacora
import cl.duoc.Bitacora.domain.validation.BitacoraValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AdministrarUiState {
    object Idle : AdministrarUiState
    object Loading : AdministrarUiState
    data class Success(val mensaje: String) : AdministrarUiState
    data class Error(val mensaje: String) : AdministrarUiState
}

class AdministrarBitacorasViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val bitacoraDao =
        BitacoraDatabase.obtenerInstancia(application).bitacoraDao()

    private val repository = BitacoraRepository(bitacoraDao)

    private val _uiState = MutableStateFlow<AdministrarUiState>(AdministrarUiState.Idle)
    val uiState: StateFlow<AdministrarUiState> = _uiState.asStateFlow()

    val bitacoras = repository.obtenerTodasLasBitacorasRoom()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var mensajeResultado by mutableStateOf("")
        private set

    var guardando by mutableStateOf(false)
        private set

    var errorAsignatura by mutableStateOf<String?>(null)
        private set

    var errorMes by mutableStateOf<String?>(null)
        private set

    var errorAnio by mutableStateOf<String?>(null)
        private set

    var errorCurso by mutableStateOf<String?>(null)
        private set

    fun crearBitacora(
        asignatura: String,
        mes: String,
        anio: String,
        curso: String
    ) {
        errorAsignatura = BitacoraValidator.validarAsignatura(asignatura)
        errorMes = BitacoraValidator.validarMes(mes)
        errorAnio = BitacoraValidator.validarAnio(anio)
        errorCurso = BitacoraValidator.validarCurso(curso)

        if (
            errorAsignatura != null ||
            errorMes != null ||
            errorAnio != null ||
            errorCurso != null
        ) {
            mensajeResultado = "Corrige los campos indicados"
            _uiState.value = AdministrarUiState.Error(mensajeResultado)
            return
        }

        if (guardando) return

        viewModelScope.launch {
            guardando = true
            _uiState.value = AdministrarUiState.Loading

            try {
                val nuevaBitacora = Bitacora(
                    id = 0,
                    asignatura = asignatura.trim(),
                    mes = mes.trim(),
                    anio = anio.toInt(),
                    curso = curso.trim(),
                    estado = EstadoBitacora.BORRADOR
                )

                repository.guardarBitacora(nuevaBitacora)
                mensajeResultado = "Bitácora creada correctamente"
                _uiState.value = AdministrarUiState.Success(mensajeResultado)

            } catch (e: Exception) {
                mensajeResultado = "Error al crear la bitácora"
                _uiState.value = AdministrarUiState.Error(mensajeResultado)

            } finally {
                guardando = false
            }
        }
    }

    fun publicarBitacora(id: Int) {
        viewModelScope.launch {
            _uiState.value = AdministrarUiState.Loading
            try {
                val publicada = repository.publicarBitacora(id)
                if (publicada) {
                    mensajeResultado = "Bitácora publicada correctamente"
                    _uiState.value = AdministrarUiState.Success(mensajeResultado)
                } else {
                    mensajeResultado = "No se pudo publicar la bitácora"
                    _uiState.value = AdministrarUiState.Error(mensajeResultado)
                }
            } catch (e: Exception) {
                mensajeResultado = "Error al publicar la bitácora"
                _uiState.value = AdministrarUiState.Error(mensajeResultado)
            }
        }
    }
}
