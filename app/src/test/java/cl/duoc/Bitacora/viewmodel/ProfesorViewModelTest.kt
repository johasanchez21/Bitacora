package cl.duoc.Bitacora.viewmodel

import cl.duoc.Bitacora.data.repository.BitacoraRepository
import cl.duoc.Bitacora.data.repository.CursoRepository
import cl.duoc.Bitacora.data.repository.EntradaBitacoraRepository
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import cl.duoc.Bitacora.ui.screens.profesor.ProfesorViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfesorViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val cursoRepository: CursoRepository = mockk()
    private val bitacoraRepository: BitacoraRepository = mockk()
    private val entradaRepository: EntradaBitacoraRepository = mockk()
    private lateinit var viewModel: ProfesorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { cursoRepository.obtenerTodosLosCursos() } returns flowOf(emptyList())
        coEvery { bitacoraRepository.obtenerTodasLasBitacorasRoom() } returns flowOf(emptyList())
        coEvery { entradaRepository.obtenerEntradasEnviadas() } returns flowOf(emptyList())
        viewModel = ProfesorViewModel(cursoRepository, bitacoraRepository, entradaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registrarCurso exitoso actualiza mensaje de exito`() {
        viewModel.actualizarNombreCurso("3° Medio A")
        viewModel.actualizarCodigoCurso("3MA")
        viewModel.actualizarAnioCurso("2025")

        coEvery { cursoRepository.registrarCurso(any()) } returns Result.success(1L)

        viewModel.registrarCurso()

        assertTrue(viewModel.exitoCurso.contains("Curso registrado con éxito"))
    }

    @Test
    fun `asignarBitacora exitoso`() {
        val profesor = Usuario(id = 2, nombre = "Prof. Juan", correo = "juan@profesor.cl", password = "pass", rol = RolUsuario.Profesor)
        val curso = Curso(id = 1, nombre = "1° A", codigo = "1A", anio = 2025)

        viewModel.actualizarAsignatura("Historia")
        viewModel.seleccionarCursoBitacora(curso)

        coEvery { bitacoraRepository.asignarBitacoraPorProfesor(profesor, any(), curso) } returns Result.success(10L)

        viewModel.asignarBitacora(profesor)

        assertTrue(viewModel.exitoBitacora.contains("creada y asignada"))
    }
}
