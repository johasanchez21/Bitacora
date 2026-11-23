package cl.duoc.Bitacora.data.repository

import cl.duoc.Bitacora.data.remote.external.ExternalApiClient
import cl.duoc.Bitacora.data.remote.external.FeriadoDto
import cl.duoc.Bitacora.data.remote.external.FeriadosApiService
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeriadosRepository(
    private val apiService: FeriadosApiService = ExternalApiClient.feriadosService
) {

    suspend fun obtenerProximoFeriado(): Result<FeriadoDto?> {
        return try {
            val response = apiService.obtenerFeriados()
            if (response.isSuccessful && response.body() != null) {
                val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                val listaFeriados = response.body()!!.data
                val proximo = listaFeriados
                    .filter { it.date >= hoy }
                    .minByOrNull { it.date }
                    ?: listaFeriados.lastOrNull()
                Result.success(proximo)
            } else {
                Result.failure(Exception("Error al consultar la API de feriados"))
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }
}
