package cl.duoc.Bitacora.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.EntradaBitacoraRepository
import cl.duoc.Bitacora.data.local.BitacoraDatabase
import cl.duoc.Bitacora.model.EntradaBitacora
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class EntradasEnviadasViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao = BitacoraDatabase
        .obtenerInstancia(application)
        .entradaBitacoraDao()

    private val repository = EntradaBitacoraRepository(dao)

    var comentarioProfesor by mutableStateOf("")
        private set

    var mensajeResultado by mutableStateOf("")
        private set

    var revisandoEntrada by mutableStateOf(false)
        private set

    val entradasEnviadas: StateFlow<List<EntradaBitacora>> =
        repository.obtenerEntradasEnviadas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun actualizarComentario(nuevoComentario: String) {
        comentarioProfesor = nuevoComentario
        mensajeResultado = ""
    }


    fun revisarEntrada(entradaId: Int) {

        if (revisandoEntrada) {
            return
        }

        if (comentarioProfesor.isBlank()) {
            mensajeResultado = "Debes escribir un comentario"
            return
        }

        revisandoEntrada = true
        mensajeResultado = ""

        viewModelScope.launch {
            try {

                val revisada = repository.revisarEntrada(
                    entradaId = entradaId,
                    comentarioProfesor = comentarioProfesor
                )

                if (revisada) {
                    mensajeResultado = "Entrada revisada correctamente"
                    comentarioProfesor = ""
                } else {
                    mensajeResultado = "No se pudo revisar la entrada"
                }

            } catch (e: Exception) {
                mensajeResultado = "Error al guardar la revisión"
            } finally {
                revisandoEntrada = false
            }
        }
    }


}

