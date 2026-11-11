package cl.duoc.Bitacora.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

sealed interface EstudianteDestino {
    object MenuPrincipal : EstudianteDestino
    object MisBitacoras : EstudianteDestino
    data class DetalleBitacora(val bitacoraId: Int) : EstudianteDestino
    data class CrearEntrada(val bitacoraId: Int, val borradorId: Int? = null) : EstudianteDestino
    object Historial : EstudianteDestino
}

class EstudianteViewModel : ViewModel() {

    var destinoActual by mutableStateOf<EstudianteDestino>(EstudianteDestino.MenuPrincipal)
        private set

    private var seleccionParaCrearEntrada = false

    fun abrirMisBitacoras() {
        seleccionParaCrearEntrada = false
        destinoActual = EstudianteDestino.MisBitacoras
    }

    fun abrirSeleccionBitacoraParaCrearEntrada() {
        seleccionParaCrearEntrada = true
        destinoActual = EstudianteDestino.MisBitacoras
    }

    fun seleccionarBitacora(bitacoraId: Int) {
        destinoActual = if (seleccionParaCrearEntrada) {
            EstudianteDestino.CrearEntrada(
                bitacoraId = bitacoraId,
                borradorId = null,
            )
        } else {
            EstudianteDestino.DetalleBitacora(bitacoraId = bitacoraId)
        }
    }

    fun abrirHistorial() {
        destinoActual = EstudianteDestino.Historial
    }

    fun editarBorrador(entradaId: Int, bitacoraId: Int) {
        destinoActual = EstudianteDestino.CrearEntrada(
            bitacoraId = bitacoraId,
            borradorId = entradaId,
        )
    }

    fun volverAMenu() {
        seleccionParaCrearEntrada = false
        destinoActual = EstudianteDestino.MenuPrincipal
    }

    fun volverAMisBitacoras() {
        destinoActual = EstudianteDestino.MisBitacoras
    }
}
