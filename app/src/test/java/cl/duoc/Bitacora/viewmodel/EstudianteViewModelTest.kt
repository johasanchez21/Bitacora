package cl.duoc.Bitacora.viewmodel

import cl.duoc.Bitacora.data.repository.BitacoraRepository
import cl.duoc.Bitacora.data.repository.CursoRepository
import cl.duoc.Bitacora.data.repository.EntradaBitacoraRepository
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.EntradaBitacora
import cl.duoc.Bitacora.domain.model.EstadoBitacora
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import cl.duoc.Bitacora.ui.screens.estudiante.EstudianteViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EstudianteViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val cursoRepository: CursoRepository = mockk()
    private val bitacoraRepository: BitacoraRepository = mockk()
    private val entradaRepository: EntradaBitacoraRepository = mockk()
    private lateinit var viewModel: EstudianteViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EstudianteViewModel(cursoRepository, bitacoraRepository, entradaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarDatosEstudiante obtiene cursos bitacoras y entradas`() {
        val estudiante = Usuario(id = 1, nombre = "Estudiante 1", correo = "e1@duoc.cl", password = "123", rol = RolUsuario.Estudiante, cursoIds = listOf(10))
        val curso = Curso(id = 10, nombre = "1° A", codigo = "1A", anio = 2025)
        val bitacora = Bitacora(id = 20, asignatura = "Química", mes = "Marzo", anio = 2025, cursoId = 10, curso = "1° A", estado = EstadoBitacora.PUBLICADA)
        val entrada = EntradaBitacora(id = 100, bitacoraId = 20, estudianteId = 1, fecha = "2025-03-30", actividad = "Lab", aprendizaje = "Reacciones", estado = EstadoEntrada.ENVIADA)

        coEvery { cursoRepository.obtenerCursoPorId(10) } returns curso
        coEvery { bitacoraRepository.obtenerTodasLasBitacorasRoom() } returns flowOf(listOf(bitacora))
        coEvery { entradaRepository.obtenerEntradasPorEstudiante(1) } returns flowOf(listOf(entrada))

        viewModel.cargarDatosEstudiante(estudiante)

        assertEquals(1, viewModel.cursosEstudiante.value.size)
        assertEquals(1, viewModel.bitacorasDisponibles.value.size)
        assertEquals(1, viewModel.misEntradas.value.size)
    }

    @Test
    fun `cargarBorradorParaEditar carga datos en el formulario`() {
        val entrada = EntradaBitacora(id = 5, bitacoraId = 10, estudianteId = 1, fecha = "2025-03-30", actividad = "Guía", aprendizaje = "Mate", dudas = "Ninguna", estado = EstadoEntrada.BORRADOR)

        viewModel.cargarBorradorParaEditar(entrada)

        assertEquals(5, viewModel.borradorIdSeleccionado)
        assertEquals("Guía", viewModel.actividadInput)
        assertEquals("Mate", viewModel.aprendizajeInput)
    }
}
