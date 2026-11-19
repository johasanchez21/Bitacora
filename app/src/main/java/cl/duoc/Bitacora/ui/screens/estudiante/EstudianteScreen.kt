package cl.duoc.Bitacora.ui.screens.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    val cursos by viewModel.cursosEstudiante.collectAsState()
    val bitacoras by viewModel.bitacorasDisponibles.collectAsState()
    val misEntradas by viewModel.misEntradas.collectAsState()

    var dropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Panel de Estudiante",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Bienvenido, ${usuarioEstudiante.nombre}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                OutlinedButton(onClick = onCerrarSesion) {
                    Text("Cerrar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cursos Asignados (Regla: Mínimo 1):",
                style = MaterialTheme.typography.titleSmall
            )
            if (cursos.isEmpty()) {
                Text("Cursos IDs pertenecientes: ${usuarioEstudiante.cursoIds}")
            } else {
                cursos.forEach { c ->
                    Text("• ${c.nombre} (${c.codigo})", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
        }

        // SECCIÓN CREAR ENTRADA
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Nueva Entrada de Bitácora",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = viewModel.bitacoraSeleccionada?.let { "${it.asignatura} - ${it.curso}" } ?: "Seleccionar Bitácora...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bitácora Asignada a tu Curso") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            if (bitacoras.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay bitácoras asignadas por profesores") },
                                    onClick = { dropdownExpanded = false }
                                )
                            } else {
                                bitacoras.forEach { b ->
                                    DropdownMenuItem(
                                        text = { Text("${b.asignatura} (${b.curso})") },
                                        onClick = {
                                            viewModel.seleccionarBitacora(b)
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.fechaInput,
                        onValueChange = { viewModel.actualizarFecha(it) },
                        label = { Text("Fecha (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.actividadInput,
                        onValueChange = { viewModel.actualizarActividad(it) },
                        label = { Text("Actividad Realizada") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.aprendizajeInput,
                        onValueChange = { viewModel.actualizarAprendizaje(it) },
                        label = { Text("¿Qué aprendiste hoy?") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.dudasInput,
                        onValueChange = { viewModel.actualizarDudas(it) },
                        label = { Text("Dudas o consultas (Opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (viewModel.mensajeError.isNotEmpty()) {
                        Text(viewModel.mensajeError, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (viewModel.mensajeExito.isNotEmpty()) {
                        Text(viewModel.mensajeExito, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Button(
                        onClick = { viewModel.guardarYEnviarEntrada(usuarioEstudiante) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enviar Entrada al Profesor")
                    }
                }
            }
        }

        // SECCIÓN HISTORIAL DE ENTRADAS DEL ESTUDIANTE
        item {
            Text(
                text = "Mis Entradas Registradas",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (misEntradas.isEmpty()) {
            item {
                Text("Aún no has registrado entradas de bitácora.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(misEntradas) { entrada ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Fecha: ${entrada.fecha}", style = MaterialTheme.typography.titleSmall)
                        Text("Estado: ${entrada.estado}")
                        Text("Actividad: ${entrada.actividad}")
                        Text("Aprendizaje: ${entrada.aprendizaje}")
                        if (entrada.comentarioProfesor.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Comentario Profesor: ${entrada.comentarioProfesor}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
