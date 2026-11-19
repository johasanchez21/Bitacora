package cl.duoc.Bitacora.ui.screens.profesor

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesorScreen(
    usuarioProfesor: Usuario,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfesorViewModel = viewModel()
) {
    val cursos by viewModel.cursos.collectAsState()
    val bitacoras by viewModel.bitacoras.collectAsState()
    val entradasEnviadas by viewModel.entradasEnviadas.collectAsState()

    var dropdownExpanded by remember { mutableStateOf(false) }
    var comentarioProfesor by remember { mutableStateOf("") }
    var entradaAComentarId by remember { mutableStateOf<Int?>(null) }

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
                        text = "Panel de Profesor",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Bienvenido, ${usuarioProfesor.nombre}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                OutlinedButton(onClick = onCerrarSesion) {
                    Text("Cerrar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
        }

        // SECCIÓN 1: Registrar Nuevo Curso (Regla 1: Máximo 2 por año)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Registrar Nuevo Curso (Regla: Máx. 2 por año)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.nombreCurso,
                        onValueChange = { viewModel.actualizarNombreCurso(it) },
                        label = { Text("Nombre del Curso (ej. 1° Medio A)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.codigoCurso,
                        onValueChange = { viewModel.actualizarCodigoCurso(it) },
                        label = { Text("Código (ej. 1MA)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = viewModel.anioCurso,
                            onValueChange = { viewModel.actualizarAnioCurso(it) },
                            label = { Text("Año") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = viewModel.semestreCurso,
                            onValueChange = { viewModel.actualizarSemestreCurso(it) },
                            label = { Text("Semestre") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (viewModel.errorCurso.isNotEmpty()) {
                        Text(viewModel.errorCurso, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (viewModel.exitoCurso.isNotEmpty()) {
                        Text(viewModel.exitoCurso, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Button(
                        onClick = { viewModel.registrarCurso() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar Curso")
                    }
                }
            }
        }

        // SECCIÓN 2: Crear y Asignar Bitácora (Regla 3: Asignada a un curso por el profesor)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Asignar Bitácora a Curso (Regla: Por Profesor)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.asignaturaBitacora,
                        onValueChange = { viewModel.actualizarAsignatura(it) },
                        label = { Text("Asignatura (ej. Matemáticas)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = viewModel.cursoSeleccionado?.nombre ?: "Seleccionar Curso...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Curso Asignado") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            if (cursos.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay cursos registrados") },
                                    onClick = { dropdownExpanded = false }
                                )
                            } else {
                                cursos.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text("${c.nombre} (${c.codigo} - ${c.anio})") },
                                        onClick = {
                                            viewModel.seleccionarCursoBitacora(c)
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (viewModel.errorBitacora.isNotEmpty()) {
                        Text(viewModel.errorBitacora, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (viewModel.exitoBitacora.isNotEmpty()) {
                        Text(viewModel.exitoBitacora, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Button(
                        onClick = { viewModel.asignarBitacora(usuarioProfesor) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crear y Asignar Bitácora")
                    }
                }
            }
        }

        // SECCIÓN 3: Entradas Enviadas por Alumnos para Revisión
        item {
            Text(
                text = "3. Entradas de Estudiantes por Revisar",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (entradasEnviadas.isEmpty()) {
            item {
                Text("No hay entradas pendientes de revisión.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(entradasEnviadas) { entrada ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Estudiante ID: ${entrada.estudianteId}", style = MaterialTheme.typography.titleSmall)
                        Text("Fecha: ${entrada.fecha}")
                        Text("Actividad: ${entrada.actividad}")
                        Text("Aprendizaje: ${entrada.aprendizaje}")
                        if (entrada.dudas.isNotBlank()) {
                            Text("Dudas: ${entrada.dudas}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (entradaAComentarId == entrada.id) {
                            OutlinedTextField(
                                value = comentarioProfesor,
                                onValueChange = { comentarioProfesor = it },
                                label = { Text("Comentario de revisión") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    viewModel.revisarEntrada(entrada.id, comentarioProfesor) {
                                        entradaAComentarId = null
                                        comentarioProfesor = ""
                                    }
                                }
                            ) {
                                Text("Guardar Revisión")
                            }
                        } else {
                            Button(
                                onClick = { entradaAComentarId = entrada.id }
                            ) {
                                Text("Revisar y Comentar")
                            }
                        }
                    }
                }
            }
        }
    }
}
