package cl.duoc.Bitacora.viewmodel

import cl.duoc.Bitacora.data.repository.UsuarioRepository
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import cl.duoc.Bitacora.ui.viewmodel.LoginUiState
import cl.duoc.Bitacora.ui.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: UsuarioRepository = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `iniciarSesion con campos vacios retorna false y estado error`() {
        val res = viewModel.iniciarSesion()
        assertFalse(res)
        assertTrue(viewModel.uiState.value is LoginUiState.Error)
    }

    @Test
    fun `iniciarSesion exitoso actualiza estado a Success`() {
        val u = Usuario(id = 1, nombre = "Sergio", correo = "sergio@duoc.cl", password = "pass", rol = RolUsuario.Estudiante)
        viewModel.actualiazrCorreo("sergio@duoc.cl")
        viewModel.actualizarPassword("123456")

        coEvery { repository.iniciarSesion("sergio@duoc.cl", "123456") } returns u

        val res = viewModel.iniciarSesion()

        assertTrue(res)
        assertEquals(u, viewModel.usuarioActual)
        assertTrue(viewModel.uiState.value is LoginUiState.Success)
    }
}
