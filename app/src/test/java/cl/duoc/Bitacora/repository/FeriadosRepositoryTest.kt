package cl.duoc.Bitacora.repository

import cl.duoc.Bitacora.data.remote.external.FeriadoDto
import cl.duoc.Bitacora.data.remote.external.FeriadosApiService
import cl.duoc.Bitacora.data.remote.external.FeriadosResponseDto
import cl.duoc.Bitacora.data.repository.FeriadosRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

class FeriadosRepositoryTest {

    private val apiService: FeriadosApiService = mockk()
    private val repository = FeriadosRepository(apiService)

    @Test
    fun `obtenerProximoFeriado exitoso retorna feriado proximo`() = runBlocking {
        val hoy = LocalDate.now()
        val fecha1 = hoy.plusDays(5).toString()
        val fecha2 = hoy.plusDays(20).toString()

        val feriados = listOf(
            FeriadoDto(date = fecha1, title = "Feriado Cercano", type = "Religioso", inalienable = true),
            FeriadoDto(date = fecha2, title = "Feriado Lejano", type = "Civil", inalienable = true)
        )
        val resp = FeriadosResponseDto(status = "success", data = feriados)
        coEvery { apiService.obtenerFeriados() } returns Response.success(resp)

        val result = repository.obtenerProximoFeriado()

        assertTrue(result.isSuccess)
        assertEquals("Feriado Cercano", result.getOrNull()?.title)
    }

    @Test
    fun `obtenerProximoFeriado con fallo de red retorna Result failure`() = runBlocking {
        coEvery { apiService.obtenerFeriados() } throws Exception("Sin red")

        val result = repository.obtenerProximoFeriado()

        assertTrue(result.isFailure)
    }
}
