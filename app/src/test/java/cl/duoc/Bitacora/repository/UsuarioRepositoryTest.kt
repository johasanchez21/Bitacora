package cl.duoc.Bitacora.repository

import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.remote.dto.UsuarioDto
import cl.duoc.Bitacora.data.repository.UsuarioRepository
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class UsuarioRepositoryTest {

    private val apiService: BitacoraApiService = mockk()
    private val repository = UsuarioRepository(apiService)

    @Test
    fun `iniciarSesion remoto exitoso retorna usuario`() = runBlocking {
        val dto = UsuarioDto(
            id = 10L,
            nombre = "Carlos Perez",
            correo = "carlos@estudiante.cl",
            password = "pass",
            rol = "Estudiante",
            cursoIds = listOf(1L)
        )
        coEvery { apiService.iniciarSesion(any()) } returns Response.success(dto)

        val usuario = repository.iniciarSesion("carlos@estudiante.cl", "pass")

        assertNotNull(usuario)
        assertEquals("Carlos Perez", usuario?.nombre)
        assertEquals(RolUsuario.Estudiante, usuario?.rol)
    }

    @Test
    fun `iniciarSesion con fallo de red realiza fallback a lista en memoria`() = runBlocking {
        coEvery { apiService.iniciarSesion(any()) } throws Exception("Sin red")

        val usuario = repository.iniciarSesion("SerWeko@estudiante.cl", "123456")

        assertNotNull(usuario)
        assertEquals("Sergio Weko", usuario?.nombre)
    }

    @Test
    fun `registrarUsuario remoto exitoso retorna Result success`() = runBlocking {
        val u = Usuario(nombre = "Ana Maria", correo = "ana@duoc.cl", password = "pass", rol = RolUsuario.Estudiante, cursoIds = listOf(1))
        val dto = UsuarioDto(id = 5L, nombre = u.nombre, correo = u.correo, password = u.password, rol = u.rol.name, cursoIds = listOf(1L))
        coEvery { apiService.registrarUsuario(any()) } returns Response.success(dto)

        val res = repository.registrarUsuario(u)

        assertTrue(res.isSuccess)
        assertEquals(5, res.getOrNull()?.id)
    }
}
