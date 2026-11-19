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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Class
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    var tabSeleccionada by remember { mutableIntStateOf(0) }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var comentarioProfesor by remember { mutableStateOf("") }
    var entradaAComentarId by remember { mutableStateOf<Int?>(null) }
    var cursoFiltroSeleccionado by remember { mutableStateOf<Curso?>(null) }
    var dropdownFiltroExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado del Profesor
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
                            text = "Panel de Profesor",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bienvenido, ${usuarioProfesor.nombre}",
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

        // Menú de Pestañas / Funcionalidades
        PrimaryTabRow(
            selectedTabIndex = tabSeleccionada,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = tabSeleccionada == 0,
                onClick = { tabSeleccionada = 0 },
                text = { Text("Crear / Asignar") },
                icon = { Icon(Icons.Default.AddCard, contentDescription = null) }
            )
            Tab(
                selected = tabSeleccionada == 1,
                onClick = { tabSeleccionada = 1 },
                text = { Text("Bitácoras Curso") },
                icon = { Icon(Icons.Default.Class, contentDescription = null) }
            )
            Tab(
                selected = tabSeleccionada == 2,
                onClick = { tabSeleccionada = 2 },
                text = { Text("Revisar (${entradasEnviadas.size})") },
                icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null) }
            )
        }

        when (tabSeleccionada) {
            0 -> {
                // PESTAÑA 1: Crear Bitácoras y Cursos
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Formulario Crear / Registrar Curso
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Registrar Nuevo Curso (Máx. 2 por año)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = viewModel.nombreCurso,
                                    onValueChange = { viewModel.actualizarNombreCurso(it) },
                                    label = { Text("Nombre del Curso (ej. 1° Medio A)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = viewModel.codigoCurso,
                                    onValueChange = { viewModel.actualizarCodigoCurso(it) },
                                    label = { Text("Código (ej. 1MA)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = viewModel.anioCurso,
                                        onValueChange = { viewModel.actualizarAnioCurso(it) },
                                        label = { Text("Año") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = viewModel.semestreCurso,
                                        onValueChange = { viewModel.actualizarSemestreCurso(it) },
                                        label = { Text("Semestre") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
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
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Registrar Curso")
                                }
                            }
                        }
                    }

                    // Formulario Crear y Asignar Bitácora
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Crear y Asignar Bitácora",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = viewModel.asignaturaBitacora,
                                    onValueChange = { viewModel.actualizarAsignatura(it) },
                                    label = { Text("Asignatura (ej. Matemáticas)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
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
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Crear y Asignar Bitácora")
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // PESTAÑA 2: Bitácoras por Curso (Estado de Avance)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Filtrar Bitácoras por Curso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    ExposedDropdownMenuBox(
                        expanded = dropdownFiltroExpanded,
                        onExpandedChange = { dropdownFiltroExpanded = !dropdownFiltroExpanded }
                    ) {
                        OutlinedTextField(
                            value = cursoFiltroSeleccionado?.nombre ?: "Todos los Cursos",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Curso") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownFiltroExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownFiltroExpanded,
                            onDismissRequest = { dropdownFiltroExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos los Cursos") },
                                onClick = {
                                    cursoFiltroSeleccionado = null
                                    dropdownFiltroExpanded = false
                                }
                            )
                            cursos.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.nombre} (${c.codigo})") },
                                    onClick = {
                                        cursoFiltroSeleccionado = c
                                        dropdownFiltroExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    val bitacorasFiltradas = remember(bitacoras, cursoFiltroSeleccionado) {
                        val c = cursoFiltroSeleccionado
                        if (c == null) bitacoras else bitacoras.filter { it.cursoId == c.id }
                    }

                    if (bitacorasFiltradas.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "No hay bitácoras para el curso seleccionado.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(bitacorasFiltradas) { b ->
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
                                                Text(b.asignatura, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                                Text("Curso: ${b.curso} • ${b.mes} ${b.anio}", style = MaterialTheme.typography.bodyMedium)
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "En Desarrollo",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // PESTAÑA 3: Entradas Enviadas por Alumnos para Revisión
                if (entradasEnviadas.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "No hay entradas pendientes de revisión.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(entradasEnviadas) { entrada ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Estudiante ID: ${entrada.estudianteId}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("Fecha: ${entrada.fecha}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Actividad: ${entrada.actividad}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Aprendizaje: ${entrada.aprendizaje}", style = MaterialTheme.typography.bodyMedium)
                                    if (entrada.dudas.isNotBlank()) {
                                        Text("Dudas: ${entrada.dudas}", style = MaterialTheme.typography.bodyMedium)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (entradaAComentarId == entrada.id) {
                                        OutlinedTextField(
                                            value = comentarioProfesor,
                                            onValueChange = { comentarioProfesor = it },
                                            label = { Text("Comentario de revisión") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Button(
                                            onClick = {
                                                viewModel.revisarEntrada(entrada.id, comentarioProfesor) {
                                                    entradaAComentarId = null
                                                    comentarioProfesor = ""
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Guardar Revisión")
                                        }
                                    } else {
                                        Button(
                                            onClick = { entradaAComentarId = entrada.id },
                                            shape = RoundedCornerShape(10.dp)
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
        }
    }
}
