package cl.duoc.Bitacora.viewmodel

import android.app.Application
import cl.duoc.Bitacora.ui.viewmodel.AdministrarBitacorasViewModel
import cl.duoc.Bitacora.ui.viewmodel.AdministrarUiState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdministrarBitacorasViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val application: Application = mockk(relaxed = true)
    private lateinit var viewModel: AdministrarBitacorasViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AdministrarBitacorasViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `crearBitacora con campos invalidos retorna estado Error`() {
        viewModel.crearBitacora("", "", "", "")
        assertTrue(viewModel.uiState.value is AdministrarUiState.Error)
        assertEquals("Corrige los campos indicados", viewModel.mensajeResultado)
    }
}
