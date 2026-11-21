package cl.duoc.Bitacora.viewmodel

import cl.duoc.Bitacora.data.repository.UsuarioRepository
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import cl.duoc.Bitacora.ui.screens.registro.RegistroUiState
import cl.duoc.Bitacora.ui.screens.registro.RegistroViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val usuarioRepository: UsuarioRepository = mockk()
    private lateinit var viewModel: RegistroViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegistroViewModel(usuarioRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registrar estudiante sin curso falla por regla de negocio`() {
        viewModel.actualizarNombre("Ana")
        viewModel.actualizarCorreo("ana@duoc.cl")
        viewModel.actualizarPassword("123456")
        viewModel.actualizarRol(RolUsuario.Estudiante)

        val res = viewModel.registrar()

        assertFalse(res)
        assertEquals("Los estudiantes deben pertenecer a lo menos a un curso.", viewModel.mensajeError)
    }

    @Test
    fun `registrar estudiante con curso exitoso`() = runTest {
        viewModel.actualizarNombre("Ana")
        viewModel.actualizarCorreo("ana@duoc.cl")
        viewModel.actualizarPassword("123456")
        viewModel.actualizarRol(RolUsuario.Estudiante)
        viewModel.toggleSeleccionCurso(1)

        val u = Usuario(id = 5, nombre = "Ana", correo = "ana@duoc.cl", password = "123456", rol = RolUsuario.Estudiante, cursoIds = listOf(1))
        coEvery { usuarioRepository.registrarUsuario(any()) } returns Result.success(u)

        val res = viewModel.registrar()

        assertTrue(res)
        testScheduler.advanceUntilIdle()
        assertTrue(viewModel.mensajeExito.isNotEmpty() || viewModel.uiState.value is RegistroUiState.Success)
    }
}
