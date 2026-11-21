package cl.duoc.Bitacora.viewmodel

import android.app.Application
import cl.duoc.Bitacora.ui.viewmodel.EntradaBitacoraViewModel
import cl.duoc.Bitacora.ui.viewmodel.EntradaUiState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EntradaBitacoraViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val application: Application = mockk(relaxed = true)
    private lateinit var viewModel: EntradaBitacoraViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EntradaBitacoraViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validarFormulario con campos vacios retorna false`() {
        val valido = viewModel.validarFormulario()
        assertFalse(valido)
    }

    @Test
    fun `enviarEntrada con formulario invalido retorna estado Error`() {
        val res = viewModel.enviarEntrada(1, 1)
        assertFalse(res)
        assertTrue(viewModel.uiState.value is EntradaUiState.Error)
    }

    @Test
    fun `limpiarFormulario reinicia campos`() {
        viewModel.actualizarFecha("30/03/2025")
        viewModel.actualizarActividad("Actividad de prueba")

        viewModel.limpiarFormulario()

        assertTrue(viewModel.fecha.isEmpty())
        assertTrue(viewModel.actividad.isEmpty())
        assertTrue(viewModel.uiState.value is EntradaUiState.Idle)
    }
}
