package cl.duoc.Bitacora.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import cl.duoc.Bitacora.data.BitacoraRepository
import cl.duoc.Bitacora.data.EntradaBitacoraRepository
import cl.duoc.Bitacora.data.local.BitacoraDatabase
import cl.duoc.Bitacora.model.EstadoEntrada
import kotlinx.coroutines.flow.first

class PdfViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val pdfRepository = PdfRepository(application)

    private val database =
        BitacoraDatabase.obtenerInstancia(application)

    private val bitacoraRepository =
        BitacoraRepository(database.bitacoraDao())

    private val entradaRepository =
        EntradaBitacoraRepository(database.entradaBitacoraDao())

    var mensajeResultado by mutableStateOf("")
        private set

    var rutaPdf by mutableStateOf<String?>(null)
        private set

    var generandoPdf by mutableStateOf(false)
        private set

    fun generarPdfPrueba() {

        if (generandoPdf) return

        viewModelScope.launch {

            generandoPdf = true
            mensajeResultado = ""
            rutaPdf = null

            try {

                val archivo = withContext(Dispatchers.IO) {
                    pdfRepository.generarPdfPrueba()
                }

                rutaPdf = archivo.absolutePath

                mensajeResultado =
                    "PDF generado correctamente"

            } catch (e: Exception) {

                mensajeResultado =
                    "Error al generar el PDF"

            } finally {

                generandoPdf = false

            }
        }
    }

    fun generarInformeMensual(
        estudianteId: Int,
        bitacoraId: Int
    ) {
        if (generandoPdf) return

        viewModelScope.launch {
            generandoPdf = true
            mensajeResultado = ""
            rutaPdf = null

            try {
                val bitacora =
                    bitacoraRepository.obtenerBitacoraPorIdRoom(
                        bitacoraId
                    )

                if (bitacora == null) {
                    mensajeResultado = "No se encontró la bitácora"
                    return@launch
                }

                val entradas =
                    entradaRepository
                        .obtenerEntradasPorEstudiante(estudianteId)
                        .first()
                        .filter { entrada ->
                            entrada.bitacoraId == bitacoraId &&
                                    entrada.estado != EstadoEntrada.BORRADOR
                        }

                if (entradas.isEmpty()) {
                    mensajeResultado =
                        "No hay entradas enviadas para este informe"
                    return@launch
                }

                val archivo = withContext(Dispatchers.IO) {
                    pdfRepository.generarInformeMensual(
                        bitacora = bitacora,
                        entradas = entradas
                    )
                }

                rutaPdf = archivo.absolutePath
                mensajeResultado = "Informe mensual generado correctamente"

            } catch (e: Exception) {
                mensajeResultado = "Error al generar el informe mensual"
            } finally {
                generandoPdf = false
            }
        }
    }
}
