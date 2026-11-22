package cl.duoc.Bitacora.ui.screens.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import cl.duoc.Bitacora.domain.model.Usuario

enum class VistaEstudiante {
    MENU,
    ENTRADAS_PENDIENTES,
    BORRADORES,
    HISTORIAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteScreen(
    usuarioEstudiante: Usuario,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EstudianteViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(usuarioEstudiante) {
        viewModel.cargarDatosEstudiante(usuarioEstudiante, context)
    }

    val bitacoras by viewModel.bitacorasDisponibles.collectAsState()
    val misEntradas by viewModel.misEntradas.collectAsState()

    var vistaActual by remember { mutableStateOf(VistaEstudiante.MENU) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    val borradores = remember(misEntradas) {
        misEntradas.filter { it.estado == EstadoEntrada.BORRADOR }
    }

    val entradasCompletadas = remember(misEntradas) {
        misEntradas.filter { it.estado == EstadoEntrada.ENVIADA || it.estado == EstadoEntrada.REVISADA }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estudiante: ${usuarioEstudiante.nombre}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Panel de Control de Bitácoras",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = onCerrarSesion,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }

        AnimatedVisibility(visible = viewModel.mensajeExito.isNotEmpty() && !mostrarFormulario) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = viewModel.mensajeExito,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Vistas del Estudiante
        when (vistaActual) {
            VistaEstudiante.MENU -> {
                Text(
                    text = "Menú Principal de Funciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        OpcionMenuCard(
                            titulo = "Entradas / Bitácoras Pendientes",
                            subtitulo = "${bitacoras.size} bitácoras por realizar (Estado: Vacía o En borrador)",
                            icono = Icons.Default.PendingActions,
                            colorContenedor = MaterialTheme.colorScheme.primaryContainer,
                            onClick = { vistaActual = VistaEstudiante.ENTRADAS_PENDIENTES }
                        )
                    }
                    item {
                        OpcionMenuCard(
                            titulo = "Bitácoras en Borrador",
                            subtitulo = "${borradores.size} borradores guardados listos para continuar",
                            icono = Icons.Default.Edit,
                            colorContenedor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = { vistaActual = VistaEstudiante.BORRADORES }
                        )
                    }
                    item {
                        OpcionMenuCard(
                            titulo = "Historial / Bitácoras Realizadas",
                            subtitulo = "${entradasCompletadas.size} entradas completadas y enviadas",
                            icono = Icons.Default.History,
                            colorContenedor = MaterialTheme.colorScheme.tertiaryContainer,
                            onClick = { vistaActual = VistaEstudiante.HISTORIAL }
                        )
                    }
                }
            }

            VistaEstudiante.ENTRADAS_PENDIENTES -> {
                HeaderSubpantalla(
                    titulo = "Entradas Pendientes de Realizar",
                    onVolver = { vistaActual = VistaEstudiante.MENU; mostrarFormulario = false }
                )

                // Formulario de Entrada
                AnimatedVisibility(visible = mostrarFormulario) {
                    FormularioEntradaCard(
                        viewModel = viewModel,
                        usuarioEstudiante = usuarioEstudiante,
                        onCancelar = { mostrarFormulario = false },
                        onExito = { mostrarFormulario = false },
                        onGuardarBorradorExitoso = {
                            vistaActual = VistaEstudiante.BORRADORES
                            mostrarFormulario = false
                        }
                    )
                }

                if (bitacoras.isEmpty()) {
                    MensajeVacio("No tienes bitácoras pendientes asignadas por tus profesores.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(bitacoras) { b ->
                            val borradorAsociado = borradores.find { it.bitacoraId == b.id }
                            val estadoTexto = if (borradorAsociado != null) "En borrador" else "Vacía / Sin iniciar"

                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(b.asignatura, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Text("Curso: ${b.curso} • ${b.mes} ${b.anio}", style = MaterialTheme.typography.bodyMedium)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = if (borradorAsociado != null) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = "Estado: $estadoTexto",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (borradorAsociado != null) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                if (borradorAsociado != null) {
                                                    viewModel.cargarBorradorParaEditar(borradorAsociado)
                                                } else {
                                                    viewModel.iniciarNuevaEntrada(b)
                                                }
                                                mostrarFormulario = true
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(if (borradorAsociado != null) Icons.Default.Edit else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (borradorAsociado != null) "Editar" else "Iniciar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            VistaEstudiante.BORRADORES -> {
                HeaderSubpantalla(
                    titulo = "Bitácoras en Borrador",
                    onVolver = { vistaActual = VistaEstudiante.MENU; mostrarFormulario = false }
                )

                // Formulario
                AnimatedVisibility(visible = mostrarFormulario) {
                    FormularioEntradaCard(
                        viewModel = viewModel,
                        usuarioEstudiante = usuarioEstudiante,
                        onCancelar = { mostrarFormulario = false },
                        onExito = { mostrarFormulario = false },
                        onGuardarBorradorExitoso = {
                            vistaActual = VistaEstudiante.BORRADORES
                            mostrarFormulario = false
                        }
                    )
                }

                if (borradores.isEmpty()) {
                    MensajeVacio("No tienes entradas guardadas en estado borrador.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(borradores) { entrada ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = MaterialTheme.colorScheme.secondaryContainer
                                            ) {
                                                Text(
                                                    text = "BORRADOR",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Fecha: ${entrada.fecha}", style = MaterialTheme.typography.bodySmall)
                                            Text("Actividad: ${entrada.actividad.ifBlank { "Sin completar" }}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.cargarBorradorParaEditar(entrada)
                                                mostrarFormulario = true
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Editar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            VistaEstudiante.HISTORIAL -> {
                HeaderSubpantalla(
                    titulo = "Historial / Bitácoras Realizadas",
                    onVolver = { vistaActual = VistaEstudiante.MENU }
                )

                if (entradasCompletadas.isEmpty()) {
                    MensajeVacio("Aún no has completado y enviado entradas de bitácora.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(entradasCompletadas) { entrada ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Fecha: ${entrada.fecha}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = if (entrada.estado == EstadoEntrada.REVISADA)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                MaterialTheme.colorScheme.tertiaryContainer
                                        ) {
                                            Text(
                                                text = entrada.estado.name,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (entrada.estado == EstadoEntrada.REVISADA)
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                else
                                                    MaterialTheme.colorScheme.onTertiaryContainer,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Actividad: ${entrada.actividad}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Aprendizaje: ${entrada.aprendizaje}", style = MaterialTheme.typography.bodyMedium)

                                    if (entrada.comentarioProfesor.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Revisión Profesor: ${entrada.comentarioProfesor}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OpcionMenuCard(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorContenedor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colorContenedor,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icono, contentDescription = null, modifier = Modifier.size(24.dp))
                    }
                }
                Column {
                    Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun HeaderSubpantalla(
    titulo: String,
    onVolver: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MensajeVacio(texto: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FormularioEntradaCard(
    viewModel: EstudianteViewModel,
    usuarioEstudiante: Usuario,
    onCancelar: () -> Unit,
    onExito: () -> Unit,
    onGuardarBorradorExitoso: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (viewModel.borradorIdSeleccionado != null) "Editar Borrador" else "Nueva Entrada",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onCancelar) {
                    Text("Cancelar")
                }
            }

            viewModel.bitacoraSeleccionada?.let { b ->
                Text("Bitácora: ${b.asignatura} (${b.curso})", style = MaterialTheme.typography.bodyMedium)
            }

            OutlinedTextField(
                value = viewModel.fechaInput,
                onValueChange = { viewModel.actualizarFecha(it) },
                label = { Text("Fecha (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.actividadInput,
                onValueChange = { viewModel.actualizarActividad(it) },
                label = { Text("Actividad Realizada") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = viewModel.aprendizajeInput,
                onValueChange = { viewModel.actualizarAprendizaje(it) },
                label = { Text("¿Qué aprendiste hoy?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = viewModel.dudasInput,
                onValueChange = { viewModel.actualizarDudas(it) },
                label = { Text("Dudas o consultas (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (viewModel.mensajeError.isNotEmpty()) {
                Text(viewModel.mensajeError, color = MaterialTheme.colorScheme.error)
            }

            if (viewModel.mensajeExito.isNotEmpty()) {
                Text(viewModel.mensajeExito, color = MaterialTheme.colorScheme.primary)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.guardarBorrador(usuarioEstudiante, onExito = onGuardarBorradorExitoso)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Guardar Borrador")
                }

                Button(
                    onClick = {
                        viewModel.guardarYEnviarEntrada(usuarioEstudiante, onExito = onExito)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Enviar Entrada")
                }
            }
        }
    }
}
