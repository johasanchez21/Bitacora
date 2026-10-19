package cl.duoc.Bitacora.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.BitacoraRepository
import cl.duoc.Bitacora.data.local.BitacoraDatabase
import cl.duoc.Bitacora.model.Bitacora
import cl.duoc.Bitacora.model.EstadoBitacora
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import cl.duoc.Bitacora.validation.BitacoraValidator

class AdministrarBitacorasViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val bitacoraDao =
        BitacoraDatabase.obtenerInstancia(application).bitacoraDao()

    private val repository = BitacoraRepository(bitacoraDao)

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

        errorAsignatura =
            BitacoraValidator.validarAsignatura(asignatura)

        errorMes =
            BitacoraValidator.validarMes(mes)

        errorAnio =
            BitacoraValidator.validarAnio(anio)
        errorCurso =
            BitacoraValidator.validarCurso(curso)

        if (
            errorAsignatura != null ||
            errorMes != null ||
            errorAnio != null ||
            errorCurso != null
        ) {
            mensajeResultado = "Corrige los campos indicados"
            return
        }

        if (guardando) return

        viewModelScope.launch {

            guardando = true

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

                mensajeResultado =
                    "Bitácora creada correctamente"

            } catch (e: Exception) {

                mensajeResultado =
                    "Error al crear la bitácora"

            } finally {

                guardando = false

            }
        }
    }


    fun publicarBitacora(id: Int) {
        viewModelScope.launch {
            try {
                val publicada = repository.publicarBitacora(id)

                mensajeResultado = if (publicada) {
                    "Bitácora publicada correctamente"
                } else {
                    "No se pudo publicar la bitácora"
                }

            } catch (e: Exception) {
                mensajeResultado = "Error al publicar la bitácora"
            }
        }
    }

}
