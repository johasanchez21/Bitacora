package cl.duoc.Bitacora.ui.screens.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import cl.duoc.Bitacora.domain.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteScreen(
    usuarioEstudiante: Usuario,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EstudianteViewModel = viewModel()
) {
    LaunchedEffect(usuarioEstudiante) {
        viewModel.cargarDatosEstudiante(usuarioEstudiante)
    }

    val bitacoras by viewModel.bitacorasDisponibles.collectAsState()
    val misEntradas by viewModel.misEntradas.collectAsState()

    var tabSeleccionada by remember { mutableIntStateOf(0) }
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
                            text = "Panel de Estudiante",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bienvenido, ${usuarioEstudiante.nombre}",
                            style = MaterialTheme.typography.bodyMedium,
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

        // Pestañas de funciones del Estudiante
        PrimaryTabRow(
            selectedTabIndex = tabSeleccionada,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = tabSeleccionada == 0,
                onClick = { tabSeleccionada = 0 },
                text = { Text("Por Realizar (${bitacoras.size})") },
                icon = { Icon(Icons.Default.Book, contentDescription = null) }
            )
            Tab(
                selected = tabSeleccionada == 1,
                onClick = { tabSeleccionada = 1 },
                text = { Text("Borradores (${borradores.size})") },
                icon = { Icon(Icons.Default.Edit, contentDescription = null) }
            )
            Tab(
                selected = tabSeleccionada == 2,
                onClick = { tabSeleccionada = 2 },
                text = { Text("Historial (${entradasCompletadas.size})") },
                icon = { Icon(Icons.Default.History, contentDescription = null) }
            )
        }

        // Formulario de Entrada (si está activo para crear o editar borrador)
        AnimatedVisibility(visible = mostrarFormulario) {
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
                        TextButton(onClick = { mostrarFormulario = false }) {
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
                            onClick = { viewModel.guardarBorrador(usuarioEstudiante) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Guardar Borrador")
                        }

                        Button(
                            onClick = {
                                viewModel.guardarYEnviarEntrada(usuarioEstudiante)
                                if (viewModel.mensajeError.isEmpty()) {
                                    mostrarFormulario = false
                                }
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

        // Vistas según la pestaña seleccionada
        when (tabSeleccionada) {
            0 -> {
                // 1. Bitácoras por Realizar
                if (bitacoras.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "No tienes bitácoras por realizar asignadas por tus profesores.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(bitacoras) { bitacora ->
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
                                        Column {
                                            Text(bitacora.asignatura, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Text("Curso: ${bitacora.curso} • ${bitacora.mes} ${bitacora.anio}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                        FilledTonalButton(
                                            onClick = {
                                                viewModel.iniciarNuevaEntrada(bitacora)
                                                mostrarFormulario = true
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Iniciar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // 2. Borradores Pendientes
                if (borradores.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "No tienes borradores guardados pendientes.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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

            2 -> {
                // 3. Bitácoras Realizadas (Historial)
                if (entradasCompletadas.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "Aún no has enviado entradas de bitácora.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                                            Text("Revisión del Profesor: ${entrada.comentarioProfesor}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
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
