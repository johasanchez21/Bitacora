package cl.duoc.Bitacora.ui.screens.estudiante

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
import cl.duoc.Bitacora.domain.model.EntradaBitacora
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import cl.duoc.Bitacora.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EstudianteViewModel(
    private val cursoRepository: CursoRepository = CursoRepository.obtenerInstancia(),
    private val bitacoraRepository: BitacoraRepository = BitacoraRepository.obtenerInstancia(),
    private val entradaRepository: EntradaBitacoraRepository = EntradaBitacoraRepository.obtenerInstancia()
) : ViewModel() {

    private val _cursosEstudiante = MutableStateFlow<List<Curso>>(emptyList())
    val cursosEstudiante: StateFlow<List<Curso>> = _cursosEstudiante.asStateFlow()

    private val _bitacorasDisponibles = MutableStateFlow<List<Bitacora>>(emptyList())
    val bitacorasDisponibles: StateFlow<List<Bitacora>> = _bitacorasDisponibles.asStateFlow()

    private val _misEntradas = MutableStateFlow<List<EntradaBitacora>>(emptyList())
    val misEntradas: StateFlow<List<EntradaBitacora>> = _misEntradas.asStateFlow()

    // Formulario Crear Entrada
    var fechaInput by mutableStateOf("2025-03-30")
        private set
    var actividadInput by mutableStateOf("")
        private set
    var aprendizajeInput by mutableStateOf("")
        private set
    var dudasInput by mutableStateOf("")
        private set
    var bitacoraSeleccionada by mutableStateOf<Bitacora?>(null)
        private set

    var mensajeError by mutableStateOf("")
        private set
    var mensajeExito by mutableStateOf("")
        private set

    fun cargarDatosEstudiante(estudiante: Usuario) {
        viewModelScope.launch {
            val listaCursos = mutableListOf<Curso>()
            estudiante.cursoIds.forEach { cId ->
                val c = cursoRepository.obtenerCursoPorId(cId)
                if (c != null) listaCursos.add(c)
            }
            _cursosEstudiante.value = listaCursos

            // Obtener bitácoras publicadas para sus cursos
            val listaBitacoras = mutableListOf<Bitacora>()
            bitacoraRepository.obtenerTodasLasBitacorasRoom().collect { todas ->
                val filtradas = todas.filter { b -> estudiante.cursoIds.contains(b.cursoId) || estudiante.cursoIds.isEmpty() }
                _bitacorasDisponibles.value = filtradas
            }
        }

        viewModelScope.launch {
            entradaRepository.obtenerEntradasPorEstudiante(estudiante.id).collect { entradas ->
                _misEntradas.value = entradas
            }
        }
    }

    fun actualizarFecha(v: String) { fechaInput = v; mensajeError = ""; mensajeExito = "" }
    fun actualizarActividad(v: String) { actividadInput = v; mensajeError = ""; mensajeExito = "" }
    fun actualizarAprendizaje(v: String) { aprendizajeInput = v; mensajeError = ""; mensajeExito = "" }
    fun actualizarDudas(v: String) { dudasInput = v; mensajeError = ""; mensajeExito = "" }
    fun seleccionarBitacora(b: Bitacora) { bitacoraSeleccionada = b; mensajeError = ""; mensajeExito = "" }

    fun guardarYEnviarEntrada(estudiante: Usuario) {
        val bitacora = bitacoraSeleccionada
        if (bitacora == null) {
            mensajeError = "Debe seleccionar una bitácora asignada a su curso."
            return
        }

        if (actividadInput.isBlank() || aprendizajeInput.isBlank()) {
            mensajeError = "Actividad y aprendizaje son campos requeridos."
            return
        }

        val nuevaEntrada = EntradaBitacora(
            bitacoraId = bitacora.id,
            estudianteId = estudiante.id,
            fecha = fechaInput,
            actividad = actividadInput,
            aprendizaje = aprendizajeInput,
            dudas = dudasInput,
            estado = EstadoEntrada.ENVIADA
        )

        viewModelScope.launch {
            val id = entradaRepository.guardarEntrada(nuevaEntrada)
            if (id > 0) {
                mensajeExito = "Entrada enviada al profesor correctamente."
                actividadInput = ""
                aprendizajeInput = ""
                dudasInput = ""
                mensajeError = ""
            } else {
                mensajeError = "Error al guardar entrada"
            }
        }
    }
}
