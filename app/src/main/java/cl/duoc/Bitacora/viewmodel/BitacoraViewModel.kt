
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BitacoraViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val bitacoraDao =
        BitacoraDatabase.obtenerInstancia(application).bitacoraDao()

    private val repository = BitacoraRepository(bitacoraDao)

    var bitacoras by mutableStateOf<List<Bitacora>>(emptyList())
        private set

    private var cargaJob: Job? = null

    fun cargarBitacoras(curso: String) {

        cargaJob?.cancel()

        cargaJob = viewModelScope.launch {

            repository.obtenerBitacorasPublicadasRoom(curso)
                .collect { lista ->

                    bitacoras = lista

                }
        }
    }
}
