package cl.duoc.Bitacora.ui.screens.profesor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.repository.BitacoraRepository
import cl.duoc.Bitacora.data.repository.CursoRepository
import cl.duoc.Bitacora.data.repository.EntradaBitacoraRepository
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.EstadoBitacora
import cl.duoc.Bitacora.domain.model.Usuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfesorViewModel(
    private val cursoRepository: CursoRepository = CursoRepository.obtenerInstancia(),
    private val bitacoraRepository: BitacoraRepository = BitacoraRepository.obtenerInstancia(),
    private val entradaRepository: EntradaBitacoraRepository = EntradaBitacoraRepository.obtenerInstancia()
) : ViewModel() {

    val cursos = cursoRepository.obtenerTodosLosCursos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bitacoras = bitacoraRepository.obtenerTodasLasBitacorasRoom().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val entradasEnviadas = entradaRepository.obtenerEntradasEnviadas().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Formulario Registrar Curso (Regla 1)
    var nombreCurso by mutableStateOf("")
        private set
    var codigoCurso by mutableStateOf("")
        private set
    var anioCurso by mutableStateOf("2025")
        private set
    var semestreCurso by mutableStateOf("1")
        private set
    var errorCurso by mutableStateOf("")
        private set
    var exitoCurso by mutableStateOf("")
        private set

    fun actualizarNombreCurso(v: String) { nombreCurso = v; errorCurso = ""; exitoCurso = "" }
    fun actualizarCodigoCurso(v: String) { codigoCurso = v; errorCurso = ""; exitoCurso = "" }
    fun actualizarAnioCurso(v: String) { anioCurso = v; errorCurso = ""; exitoCurso = "" }
    fun actualizarSemestreCurso(v: String) { semestreCurso = v; errorCurso = ""; exitoCurso = "" }

    fun registrarCurso() {
        val anioInt = anioCurso.toIntOrNull() ?: 2025
        val semInt = semestreCurso.toIntOrNull() ?: 1
        val nuevoCurso = Curso(
            nombre = nombreCurso,
            codigo = codigoCurso,
            anio = anioInt,
            semestre = semInt
        )

        viewModelScope.launch {
            val res = cursoRepository.registrarCurso(nuevoCurso)
            if (res.isSuccess) {
                exitoCurso = "Curso registrado con éxito (Año $anioInt)."
                errorCurso = ""
                nombreCurso = ""
                codigoCurso = ""
            } else {
                errorCurso = res.exceptionOrNull()?.message ?: "Error al registrar el curso"
                exitoCurso = ""
            }
        }
    }

    // Formulario Crear Bitácora (Regla 3)
    var asignaturaBitacora by mutableStateOf("")
        private set
    var mesBitacora by mutableStateOf("Marzo")
        private set
    var anioBitacora by mutableStateOf("2025")
        private set
    var cursoSeleccionado by mutableStateOf<Curso?>(null)
        private set
    var errorBitacora by mutableStateOf("")
        private set
    var exitoBitacora by mutableStateOf("")
        private set

    fun actualizarAsignatura(v: String) { asignaturaBitacora = v; errorBitacora = ""; exitoBitacora = "" }
    fun actualizarMesBitacora(v: String) { mesBitacora = v; errorBitacora = ""; exitoBitacora = "" }
    fun actualizarAnioBitacora(v: String) { anioBitacora = v; errorBitacora = ""; exitoBitacora = "" }
    fun seleccionarCursoBitacora(c: Curso) { cursoSeleccionado = c; errorBitacora = ""; exitoBitacora = "" }

    fun asignarBitacora(profesor: Usuario) {
        val curso = cursoSeleccionado
        if (curso == null) {
            errorBitacora = "Debe seleccionar un curso registrado."
            return
        }

        val anioInt = anioBitacora.toIntOrNull() ?: 2025
        val bitacora = Bitacora(
            asignatura = asignaturaBitacora,
            mes = mesBitacora,
            anio = anioInt,
            cursoId = curso.id,
            curso = curso.nombre,
            profesorId = profesor.id,
            estado = EstadoBitacora.PUBLICADA
        )

        viewModelScope.launch {
            val res = bitacoraRepository.asignarBitacoraPorProfesor(profesor, bitacora, curso)
            if (res.isSuccess) {
                exitoBitacora = "Bitácora creada y asignada al curso '${curso.nombre}'."
                errorBitacora = ""
                asignaturaBitacora = ""
            } else {
                errorBitacora = res.exceptionOrNull()?.message ?: "Error al asignar bitácora"
                exitoBitacora = ""
            }
        }
    }

    // Revisar entrada
    fun revisarEntrada(entradaId: Int, comentario: String, onFin: () -> Unit) {
        viewModelScope.launch {
            entradaRepository.revisarEntrada(entradaId, comentario)
            onFin()
        }
    }
}
