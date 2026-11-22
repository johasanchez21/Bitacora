package cl.duoc.Bitacora.repository

import cl.duoc.Bitacora.data.local.dao.EntradaBitacoraDao
import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.remote.dto.EntradaBitacoraDto
import cl.duoc.Bitacora.data.repository.EntradaBitacoraRepository
import cl.duoc.Bitacora.domain.model.EntradaBitacora
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class EntradaBitacoraRepositoryTest {

    private val apiService: BitacoraApiService = mockk()
    private val entradaDao: EntradaBitacoraDao = mockk(relaxed = true)
    private val repository = EntradaBitacoraRepository(entradaDao, apiService)

    @Test
    fun `guardarEntrada borrador se guarda en DAO de Room local`() = runBlocking {
        val borrador = EntradaBitacora(
            bitacoraId = 1,
            estudianteId = 5,
            fecha = "2025-03-30",
            actividad = "Guía de matemáticas",
            aprendizaje = "Ecuaciones",
            estado = EstadoEntrada.BORRADOR
        )
        coEvery { entradaDao.insertarEntrada(any()) } returns 12L

        val id = repository.guardarEntrada(borrador)

        assertEquals(12L, id)
    }

    @Test
    fun `enviarBorrador a API remota retorna true si la API confirma envio`() = runBlocking {
        val entrada = EntradaBitacora(
            id = 7,
            bitacoraId = 1,
            estudianteId = 5,
            fecha = "2025-03-30",
            actividad = "Informe de biología",
            aprendizaje = "Células",
            estado = EstadoEntrada.BORRADOR
        )
        val dtoResp = EntradaBitacoraDto(id = 7L, bitacoraId = 1L, estudianteId = 5L, fecha = "2025-03-30", actividad = "Informe de biología", aprendizaje = "Células", estado = "ENVIADA")
        coEvery { apiService.crearEntrada(any()) } returns Response.success(dtoResp)

        val exito = repository.enviarBorrador(entrada)

        assertTrue(exito)
    }

    @Test
    fun `enviarBorrador retorna false cuando la API responde con error HTTP`() = runBlocking {
        val entrada = EntradaBitacora(
            id = 7,
            bitacoraId = 1,
            estudianteId = 5,
            fecha = "2025-03-30",
            actividad = "Informe de biología",
            aprendizaje = "Células",
            estado = EstadoEntrada.BORRADOR
        )
        coEvery { apiService.crearEntrada(any()) } returns Response.error(500, "Server error".toResponseBody(null))

        val exito = repository.enviarBorrador(entrada)

        assertFalse(exito)
    }

    @Test
    fun `enviarBorrador retorna false cuando ocurre fallo de conexion`() = runBlocking {
        val entrada = EntradaBitacora(
            id = 7,
            bitacoraId = 1,
            estudianteId = 5,
            fecha = "2025-03-30",
            actividad = "Informe de biología",
            aprendizaje = "Células",
            estado = EstadoEntrada.BORRADOR
        )
        coEvery { apiService.crearEntrada(any()) } throws IOException("Sin conexión")

        val exito = repository.enviarBorrador(entrada)

        assertFalse(exito)
    }

    @Test
    fun `guardarEntrada devuelve -1 cuando falla el envio directo al servidor`() = runBlocking {
        val entradaEnviada = EntradaBitacora(
            bitacoraId = 1,
            estudianteId = 5,
            fecha = "2025-03-30",
            actividad = "Prueba sin red",
            aprendizaje = "Red caída",
            estado = EstadoEntrada.ENVIADA
        )
        coEvery { apiService.crearEntrada(any()) } throws IOException("Sin red")
        coEvery { entradaDao.insertarEntrada(any()) } returns 5L

        val id = repository.guardarEntrada(entradaEnviada)

        assertEquals(-1L, id)
    }

    @Test
    fun `revisarEntrada por profesor`() = runBlocking {
        val dtoResp = EntradaBitacoraDto(id = 3L, bitacoraId = 1L, estudianteId = 5L, fecha = "2025-03-30", actividad = "Actividad", aprendizaje = "Aprendizaje", estado = "REVISADA", comentarioProfesor = "Excelente trabajo")
        coEvery { apiService.revisarEntrada(3L, any()) } returns Response.success(dtoResp)

        val res = repository.revisarEntrada(3, "Excelente trabajo")

        assertTrue(res)
    }
}
