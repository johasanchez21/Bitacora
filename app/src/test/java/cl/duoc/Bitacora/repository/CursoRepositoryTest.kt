package cl.duoc.Bitacora.repository

import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.remote.dto.CursoDto
import cl.duoc.Bitacora.data.repository.CursoRepository
import cl.duoc.Bitacora.domain.model.Curso
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class CursoRepositoryTest {

    private val apiService: BitacoraApiService = mockk()
    private val repository = CursoRepository(apiService = apiService)

    @Test
    fun `registrarCurso remoto exitoso retorna id`() = runBlocking {
        val curso = Curso(nombre = "3° Medio C", codigo = "3MC", anio = 2026, semestre = 1)
        val dto = CursoDto(id = 15L, nombre = curso.nombre, codigo = curso.codigo, anio = curso.anio, semestre = curso.semestre)
        coEvery { apiService.registrarCurso(any()) } returns Response.success(dto)

        val res = repository.registrarCurso(curso)

        assertTrue(res.isSuccess)
        assertEquals(15L, res.getOrNull())
    }

    @Test
    fun `obtenerTodosLosCursos emit lista devuelta por backend`() = runBlocking {
        val listaDto = listOf(
            CursoDto(id = 1L, nombre = "1° Medio A", codigo = "1MA", anio = 2025, semestre = 1)
        )
        coEvery { apiService.obtenerCursos() } returns Response.success(listaDto)

        val cursos = repository.obtenerTodosLosCursos().first()

        assertEquals(1, cursos.size)
        assertEquals("1° Medio A", cursos.first().nombre)
    }
}
