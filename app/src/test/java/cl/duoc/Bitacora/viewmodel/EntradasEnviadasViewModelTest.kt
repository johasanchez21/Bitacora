package cl.duoc.Bitacora.viewmodel

import android.app.Application
import cl.duoc.Bitacora.ui.viewmodel.EntradasEnviadasViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EntradasEnviadasViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val application: Application = mockk(relaxed = true)
    private lateinit var viewModel: EntradasEnviadasViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EntradasEnviadasViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `actualizarComentario actualiza valor`() {
        viewModel.actualizarComentario("Buen trabajo")
        assertEquals("Buen trabajo", viewModel.comentarioProfesor)
    }

    @Test
    fun `revisarEntrada con comentario vacio exige mensaje`() {
        viewModel.revisarEntrada(1)
        assertEquals("Debes escribir un comentario", viewModel.mensajeResultado)
    }
}
