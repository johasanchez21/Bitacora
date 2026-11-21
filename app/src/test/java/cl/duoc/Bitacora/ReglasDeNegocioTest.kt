package cl.duoc.Bitacora

import cl.duoc.Bitacora.data.local.dao.BitacoraDao
import cl.duoc.Bitacora.data.local.dao.CursoDao
import cl.duoc.Bitacora.data.local.entity.BitacoraEntity
import cl.duoc.Bitacora.data.local.entity.CursoEntity
import cl.duoc.Bitacora.data.remote.BitacoraApiService
import cl.duoc.Bitacora.data.repository.BitacoraRepository
import cl.duoc.Bitacora.data.repository.CursoRepository
import cl.duoc.Bitacora.data.repository.UsuarioRepository
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasDeNegocioTest {

    private val mockApi: BitacoraApiService = mockk(relaxed = true)

    // Fake CursoDao para pruebas
    class FakeCursoDao : CursoDao {
        private val cursos = mutableListOf<CursoEntity>()

        override suspend fun insertarCurso(curso: CursoEntity): Long {
            val id = (cursos.size + 1)
            cursos.add(curso.copy(id = id))
            return id.toLong()
        }

        override fun obtenerTodosLosCursos(): Flow<List<CursoEntity>> {
            return flowOf(cursos)
        }

        override suspend fun obtenerCursosPorAnio(anio: Int): List<CursoEntity> {
            return cursos.filter { it.anio == anio }
        }

        override suspend fun contarCursosPorAnio(anio: Int): Int {
            return cursos.count { it.anio == anio }
        }

        override suspend fun obtenerCursoPorId(id: Int): CursoEntity? {
            return cursos.find { it.id == id }
        }
    }

    // Fake BitacoraDao para pruebas
    class FakeBitacoraDao : BitacoraDao {
        private val bitacoras = mutableListOf<BitacoraEntity>()

        override suspend fun insertarBitacora(bitacora: BitacoraEntity): Long {
            val id = bitacoras.size + 1
            bitacoras.add(bitacora.copy(id = id))
            return id.toLong()
        }

        override fun obtenerTodasLasBitacoras(): Flow<List<BitacoraEntity>> {
            return flowOf(bitacoras)
        }

        override fun obtenerBitacorasPublicadas(curso: String): Flow<List<BitacoraEntity>> {
            return flowOf(bitacoras.filter { it.curso == curso && it.estado == "PUBLICADA" })
        }

        override fun obtenerBitacorasPorCursoId(cursoId: Int): Flow<List<BitacoraEntity>> {
            return flowOf(bitacoras.filter { it.cursoId == cursoId })
        }

        override suspend fun obtenerBitacoraPorId(id: Int): BitacoraEntity? {
            return bitacoras.find { it.id == id }
        }

        override suspend fun actualizarEstado(id: Int, nuevoEstado: String): Int {
            val idx = bitacoras.indexOfFirst { it.id == id }
            if (idx != -1) {
                bitacoras[idx] = bitacoras[idx].copy(estado = nuevoEstado)
                return 1
            }
            return 0
        }

        override suspend fun actualizarBitacora(bitacora: BitacoraEntity) {
            val idx = bitacoras.indexOfFirst { it.id == bitacora.id }
            if (idx != -1) {
                bitacoras[idx] = bitacora
            }
        }
    }

    @Test
    fun `Regla 1 - Permite registrar hasta dos cursos por ano pero rechaza el tercero`() = runBlocking {
        val repo = CursoRepository(FakeCursoDao(), mockApi)

        val curso1 = Curso(nombre = "1° Medio A", codigo = "1MA", anio = 2026, semestre = 1)
        val curso2 = Curso(nombre = "1° Medio B", codigo = "1MB", anio = 2026, semestre = 2)
        val curso3 = Curso(nombre = "1° Medio C", codigo = "1MC", anio = 2026, semestre = 2)

        val res1 = repo.registrarCurso(curso1)
        val res2 = repo.registrarCurso(curso2)
        val res3 = repo.registrarCurso(curso3)

        assertTrue("El curso 1 debe ser registrado con éxito", res1.isSuccess)
        assertTrue("El curso 2 debe ser registrado con éxito", res2.isSuccess)
        assertTrue("El curso 3 debe ser rechazado por superar el límite de 2 cursos por año", res3.isFailure)
        assertEquals(
            "No se pueden registrar más de 2 cursos por año (El año 2026 ya tiene 2 cursos registrados).",
            res3.exceptionOrNull()?.message
        )
    }

    @Test
    fun `Regla 2 - Estudiante debe pertenecer a lo menos a un curso`() = runBlocking {
        val repo = UsuarioRepository(mockApi)

        val estudianteSinCurso = Usuario(
            nombre = "Juan Pérez",
            correo = "juan@estudiante.cl",
            password = "pass",
            rol = RolUsuario.Estudiante,
            cursoIds = emptyList()
        )

        val estudianteConCurso = Usuario(
            nombre = "María López",
            correo = "maria@estudiante.cl",
            password = "pass",
            rol = RolUsuario.Estudiante,
            cursoIds = listOf(1)
        )

        val resSin = repo.registrarUsuario(estudianteSinCurso)
        val resCon = repo.registrarUsuario(estudianteConCurso)

        assertTrue("El estudiante sin curso debe ser rechazado", resSin.isFailure)
        assertEquals(
            "El estudiante debe pertenecer a lo menos a un curso.",
            resSin.exceptionOrNull()?.message
        )
        assertTrue("El estudiante con al menos un curso debe ser registrado con éxito", resCon.isSuccess)
    }

    @Test
    fun `Regla 3 - Las bitacoras deben ser asignadas a un curso en particular por el profesor`() = runBlocking {
        val bitacoraRepo = BitacoraRepository(FakeBitacoraDao(), mockApi)

        val profesor = Usuario(id = 1, nombre = "Prof. Carlos", correo = "carlos@profesor.cl", password = "123", rol = RolUsuario.Profesor)
        val estudiante = Usuario(id = 2, nombre = "Estudiante Ana", correo = "ana@estudiante.cl", password = "123", rol = RolUsuario.Estudiante, cursoIds = listOf(10))

        val cursoValido = Curso(id = 10, nombre = "1° Medio A", codigo = "1MA", anio = 2025)
        val cursoInvalido = Curso(id = 0, nombre = "Curso No Existente", codigo = "NONE", anio = 2025)

        val bitacoraPrueba = Bitacora(
            asignatura = "Matemáticas",
            mes = "Marzo",
            anio = 2025,
            curso = "1° Medio A"
        )

        val resEstudiante = bitacoraRepo.asignarBitacoraPorProfesor(estudiante, bitacoraPrueba, cursoValido)
        assertTrue("No se debe permitir a un estudiante crear/asignar bitácoras", resEstudiante.isFailure)

        val resCursoInvalido = bitacoraRepo.asignarBitacoraPorProfesor(profesor, bitacoraPrueba, cursoInvalido)
        assertTrue("No se debe permitir asignar bitácora a un curso no registrado", resCursoInvalido.isFailure)

        val resExito = bitacoraRepo.asignarBitacoraPorProfesor(profesor, bitacoraPrueba, cursoValido)
        assertTrue("El profesor debe poder asignar una bitácora a un curso registrado", resExito.isSuccess)
    }
}
