package cl.duoc.Bitacora.repository

import cl.duoc.Bitacora.data.local.dao.BitacoraDao
import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.remote.dto.BitacoraDto
import cl.duoc.Bitacora.data.repository.BitacoraRepository
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.EstadoBitacora
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class BitacoraRepositoryTest {

    private val apiService: BitacoraApiService = mockk()
    private val bitacoraDao: BitacoraDao = mockk(relaxed = true)
    private val repository = BitacoraRepository(bitacoraDao, apiService)

    @Test
    fun `asignarBitacoraPorProfesor remoto exitoso`() = runBlocking {
        val profesor = Usuario(id = 2, nombre = "Profesor X", correo = "prof@duoc.cl", password = "123", rol = RolUsuario.Profesor)
        val curso = Curso(id = 1, nombre = "1° A", codigo = "1A", anio = 2025)
        val bitacora = Bitacora(asignatura = "Física", mes = "Abril", anio = 2025, curso = "1° A")

        val bDto = BitacoraDto(id = 99L, asignatura = "Física", mes = "Abril", anio = 2025, cursoId = 1L, curso = "1° A", profesorId = 2L, estado = "PUBLICADA")
        coEvery { apiService.crearBitacora(any()) } returns Response.success(bDto)

        val res = repository.asignarBitacoraPorProfesor(profesor, bitacora, curso)

        assertTrue(res.isSuccess)
        assertEquals(99L, res.getOrNull())
    }

    @Test
    fun `publicarBitacora actualiza estado a PUBLICADA`() = runBlocking {
        val bDto = BitacoraDto(id = 5L, asignatura = "Química", mes = "Mayo", anio = 2025, curso = "2° B", estado = "PUBLICADA")
        coEvery { apiService.publicarBitacora(5L) } returns Response.success(bDto)

        val exitoso = repository.publicarBitacora(5)

        assertTrue(exitoso)
    }

    @Test
    fun `obtenerTodasLasBitacorasRoom emite bitacoras de la API`() = runBlocking {
        val listaDto = listOf(
            BitacoraDto(id = 1L, asignatura = "Historia", mes = "Marzo", anio = 2025, curso = "7° A", estado = "PUBLICADA")
        )
        coEvery { apiService.obtenerTodasLasBitacoras() } returns Response.success(listaDto)

        val lista = repository.obtenerTodasLasBitacorasRoom().first()

        assertEquals(1, lista.size)
        assertEquals("Historia", lista.first().asignatura)
    }
}
