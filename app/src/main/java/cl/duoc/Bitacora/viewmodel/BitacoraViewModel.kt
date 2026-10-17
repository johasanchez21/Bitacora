package cl.duoc.Bitacora.viewmodel



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.Bitacora.data.BitacoraRepository
import cl.duoc.Bitacora.model.Bitacora

class BitacoraViewModel : ViewModel() {

    private val repository = BitacoraRepository()

    var bitacoras by mutableStateOf<List<Bitacora>>(emptyList())
        private set

    fun cargarBitacoras(curso: String) {
        bitacoras = repository.obtenerBitacorasPublicadas(curso)
    }
}
