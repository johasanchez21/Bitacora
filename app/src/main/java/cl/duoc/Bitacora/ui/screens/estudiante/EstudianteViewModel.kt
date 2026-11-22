package cl.duoc.Bitacora.ui.screens.estudiante

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.Bitacora.data.local.BitacoraDatabase
import cl.duoc.Bitacora.data.repository.BitacoraRepository
import cl.duoc.Bitacora.data.repository.CursoRepository
import cl.duoc.Bitacora.data.repository.EntradaBitacoraRepository
import cl.duoc.Bitacora.data.repository.FotoRepository
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.EntradaBitacora
import cl.duoc.Bitacora.domain.model.EstadoBitacora
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

    // Formulario Crear / Editar Entrada
    var borradorIdSeleccionado by mutableStateOf<Int?>(null)
        private set
    var fechaInput by mutableStateOf("2025-03-30")
        private set
    var actividadInput by mutableStateOf("")
        private set
    var aprendizajeInput by mutableStateOf("")
        private set
    var dudasInput by mutableStateOf("")
        private set
    var fotosUrisInput by mutableStateOf<List<String>>(emptyList())
        private set
    var guardandoFoto by mutableStateOf(false)
        private set
    var errorFoto by mutableStateOf<String?>(null)
        private set

    var bitacoraSeleccionada by mutableStateOf<Bitacora?>(null)
        private set

    var mensajeError by mutableStateOf("")
        private set
    var mensajeExito by mutableStateOf("")
        private set

    fun inicializarConContexto(context: Context) {
        val db = BitacoraDatabase.obtenerInstancia(context)
        entradaRepository.dao = db.entradaBitacoraDao()
    }

    fun cargarDatosEstudiante(estudiante: Usuario, context: Context? = null) {
        if (context != null) {
            inicializarConContexto(context)
        }

        viewModelScope.launch {
            val listaCursos = mutableListOf<Curso>()
            estudiante.cursoIds.forEach { cId ->
                val c = cursoRepository.obtenerCursoPorId(cId)
                if (c != null) listaCursos.add(c)
            }
            _cursosEstudiante.value = listaCursos

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

    fun agregarFotografia(uriSeleccionada: Uri, context: Context) {
        if (guardandoFoto) return
        if (fotosUrisInput.size >= 3) {
            errorFoto = "Solo se permiten hasta 3 imágenes por entrada."
            return
        }

        viewModelScope.launch {
            guardandoFoto = true
            errorFoto = null

            try {
                val repo = FotoRepository(context)
                val rutaGuardada = repo.guardarFoto(uriSeleccionada)
                if (rutaGuardada != null) {
                    fotosUrisInput = (fotosUrisInput + rutaGuardada).take(3)
                } else {
                    errorFoto = "No se pudo procesar la fotografía"
                }
            } catch (_: Exception) {
                errorFoto = "Error al guardar la fotografía"
            } finally {
                guardandoFoto = false
            }
        }
    }

    fun eliminarFotografia(index: Int) {
        if (index in fotosUrisInput.indices) {
            fotosUrisInput = fotosUrisInput.toMutableList().apply { removeAt(index) }
            errorFoto = null
        }
    }

    fun iniciarNuevaEntrada(bitacora: Bitacora) {
        limpiarFormulario()
        bitacoraSeleccionada = bitacora
    }

    fun cargarBorradorParaEditar(entrada: EntradaBitacora) {
        borradorIdSeleccionado = entrada.id
        fechaInput = entrada.fecha
        actividadInput = entrada.actividad
        aprendizajeInput = entrada.aprendizaje
        dudasInput = entrada.dudas
        fotosUrisInput = entrada.obtenerListaFotos()
        bitacoraSeleccionada = _bitacorasDisponibles.value.find { it.id == entrada.bitacoraId }
            ?: Bitacora(
                id = entrada.bitacoraId,
                asignatura = "Bitácora",
                mes = "Marzo",
                anio = 2025,
                cursoId = 1,
                curso = "1° Medio A",
                profesorId = 2,
                estado = EstadoBitacora.PUBLICADA
            )
        mensajeError = ""
        mensajeExito = "Borrador cargado para edición"
    }

    fun obtenerBitacoraValida(estudiante: Usuario): Bitacora {
        return bitacoraSeleccionada
            ?: _bitacorasDisponibles.value.firstOrNull()
            ?: Bitacora(
                id = 1,
                asignatura = "General",
                mes = "Marzo",
                anio = 2025,
                cursoId = estudiante.cursoIds.firstOrNull() ?: 1,
                curso = "1° Medio A",
                profesorId = 2,
                estado = EstadoBitacora.PUBLICADA
            )
    }

    fun guardarBorrador(estudiante: Usuario, onExito: (() -> Unit)? = null) {
        val bitacora = obtenerBitacoraValida(estudiante)

        val fotoUriString = if (fotosUrisInput.isNotEmpty()) fotosUrisInput.joinToString(";") else null
        val nuevaEntrada = EntradaBitacora(
            id = borradorIdSeleccionado ?: 0,
            bitacoraId = bitacora.id,
            estudianteId = estudiante.id,
            fecha = fechaInput,
            actividad = actividadInput,
            aprendizaje = aprendizajeInput,
            dudas = dudasInput,
            estado = EstadoEntrada.BORRADOR,
            fotoUri = fotoUriString,
            fotosUris = fotosUrisInput
        )

        viewModelScope.launch {
            try {
                val idResult: Long = if (borradorIdSeleccionado != null && borradorIdSeleccionado!! > 0) {
                    entradaRepository.actualizarBorrador(nuevaEntrada)
                    borradorIdSeleccionado!!.toLong()
                } else {
                    val nuevoId = entradaRepository.guardarEntrada(nuevaEntrada)
                    if (nuevoId > 0) {
                        borradorIdSeleccionado = nuevoId.toInt()
                    }
                    nuevoId
                }

                if (idResult > 0) {
                    mensajeExito = "Borrador guardado correctamente."
                    mensajeError = ""
                    onExito?.invoke()
                } else {
                    mensajeError = "Ocurrió un error al guardar el borrador."
                    mensajeExito = ""
                }
            } catch (_: Exception) {
                mensajeError = "Error al guardar el borrador en la base de datos."
                mensajeExito = ""
            }
        }
    }

    fun guardarYEnviarEntrada(estudiante: Usuario, onExito: (() -> Unit)? = null) {
        val bitacora = obtenerBitacoraValida(estudiante)

        if (actividadInput.isBlank() || aprendizajeInput.isBlank()) {
            mensajeError = "Actividad y aprendizaje son campos requeridos."
            return
        }

        val fotoUriString = if (fotosUrisInput.isNotEmpty()) fotosUrisInput.joinToString(";") else null
        val nuevaEntrada = EntradaBitacora(
            id = borradorIdSeleccionado ?: 0,
            bitacoraId = bitacora.id,
            estudianteId = estudiante.id,
            fecha = fechaInput,
            actividad = actividadInput,
            aprendizaje = aprendizajeInput,
            dudas = dudasInput,
            estado = EstadoEntrada.ENVIADA,
            fotoUri = fotoUriString,
            fotosUris = fotosUrisInput
        )

        viewModelScope.launch {
            try {
                val enviado: Boolean = if (borradorIdSeleccionado != null && borradorIdSeleccionado!! > 0) {
                    entradaRepository.enviarBorrador(nuevaEntrada)
                } else {
                    val id = entradaRepository.guardarEntrada(nuevaEntrada)
                    id > 0
                }

                if (enviado) {
                    limpiarFormulario()
                    mensajeExito = "Entrada enviada al profesor correctamente."
                    mensajeError = ""
                    onExito?.invoke()
                } else {
                    mensajeError = "No se pudo enviar la entrada."
                    mensajeExito = ""
                }
            } catch (_: Exception) {
                mensajeError = "Error al enviar la entrada."
                mensajeExito = ""
            }
        }
    }

    fun limpiarFormulario() {
        borradorIdSeleccionado = null
        actividadInput = ""
        aprendizajeInput = ""
        dudasInput = ""
        fotosUrisInput = emptyList()
        errorFoto = null
        guardandoFoto = false
        mensajeError = ""
        mensajeExito = ""
    }
}
