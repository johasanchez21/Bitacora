package cl.duoc.Bitacora.ui.screens.profesor

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.domain.model.Curso
import cl.duoc.Bitacora.domain.model.Usuario
import cl.duoc.Bitacora.ui.screens.estudiante.TarjetaProximoFeriado
import coil3.compose.AsyncImage
import androidx.compose.runtime.saveable.rememberSaveable

enum class VistaProfesor {
    MENU,
    CREAR_CURSO,
    CREAR_BITACORA,
    BITACORAS_CURSO,
    REVISAR_ENTRADAS
}

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

    var vistaActual by rememberSaveable { mutableStateOf(VistaProfesor.MENU) }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var comentarioProfesor by rememberSaveable { mutableStateOf("") }
    var entradaAComentarId by rememberSaveable { mutableStateOf<Int?>(null) }
    var cursoFiltroId by rememberSaveable { mutableStateOf<Int?>(null) }
    val cursoFiltroSeleccionado = remember(cursos, cursoFiltroId) {
        cursos.find { it.id == cursoFiltroId }
    }
    var dropdownFiltroExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
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
                            text = "Profesor: ${usuarioProfesor.nombre}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Panel de Gestión de Cursos y Bitácoras",
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

        when (vistaActual) {
            VistaProfesor.MENU -> {
                Text(
                    text = "Menú Principal de Funciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Tarjeta del Próximo Feriado (API Externa)
                TarjetaProximoFeriado()

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        OpcionMenuProfesorCard(
                            titulo = "1. Crear / Registrar Curso",
                            subtitulo = "${cursos.size} cursos registrados (Máximo 2 por año)",
                            icono = Icons.Default.School,
                            colorContenedor = MaterialTheme.colorScheme.primaryContainer,
                            onClick = { vistaActual = VistaProfesor.CREAR_CURSO }
                        )
                    }
                    item {
                        OpcionMenuProfesorCard(
                            titulo = "2. Crear y Asignar Bitácora",
                            subtitulo = "Asignar nueva bitácora mensual a un curso",
                            icono = Icons.Default.AddCard,
                            colorContenedor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = { vistaActual = VistaProfesor.CREAR_BITACORA }
                        )
                    }
                    item {
                        OpcionMenuProfesorCard(
                            titulo = "3. Ver Bitácoras por Curso",
                            subtitulo = "${bitacoras.size} bitácoras creadas • Consultar estado de avance",
                            icono = Icons.Default.Class,
                            colorContenedor = MaterialTheme.colorScheme.tertiaryContainer,
                            onClick = { vistaActual = VistaProfesor.BITACORAS_CURSO }
                        )
                    }
                    item {
                        OpcionMenuProfesorCard(
                            titulo = "4. Bitácoras / Entradas a Revisar",
                            subtitulo = "${entradasEnviadas.size} entradas enviadas pendientes de revisión",
                            icono = Icons.Default.AssignmentTurnedIn,
                            colorContenedor = MaterialTheme.colorScheme.errorContainer,
                            onClick = { vistaActual = VistaProfesor.REVISAR_ENTRADAS }
                        )
                    }
                }
            }

            VistaProfesor.CREAR_CURSO -> {
                HeaderProfesorSubpantalla(
                    titulo = "Crear / Registrar Curso",
                    onVolver = { vistaActual = VistaProfesor.MENU }
                )

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Registrar Nuevo Curso (Regla: Máx. 2 por año)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = viewModel.nombreCurso,
                                onValueChange = { viewModel.actualizarNombreCurso(it) },
                                label = { Text("Nombre del Curso (ej. 1° Medio A)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = viewModel.codigoCurso,
                                onValueChange = { viewModel.actualizarCodigoCurso(it) },
                                label = { Text("Código (ej. 1MA)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

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

                            if (viewModel.errorCurso.isNotEmpty()) {
                                Text(viewModel.errorCurso, color = MaterialTheme.colorScheme.error)
                            }

                            if (viewModel.exitoCurso.isNotEmpty()) {
                                Text(viewModel.exitoCurso, color = MaterialTheme.colorScheme.primary)
                            }

                            Button(
                                onClick = { viewModel.registrarCurso() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Guardar y Registrar Curso")
                            }
                        }
                    }

                    Text(
                        text = "Cursos Registrados (${cursos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (cursos.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Aún no hay cursos registrados en el sistema.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            cursos.forEach { c ->
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
                                                Text(
                                                    text = c.nombre,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Código: ${c.codigo} • Semestre ${c.semestre}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "Año ${c.anio}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
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

            VistaProfesor.CREAR_BITACORA -> {
                HeaderProfesorSubpantalla(
                    titulo = "Crear y Asignar Bitácora",
                    onVolver = { vistaActual = VistaProfesor.MENU }
                )

                val scrollState = rememberScrollState()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Asignar Nueva Bitácora a Curso",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = viewModel.asignaturaBitacora,
                            onValueChange = { viewModel.actualizarAsignatura(it) },
                            label = { Text("Asignatura (ej. Matemáticas)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

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
                                        text = { Text("No hay cursos registrados aún") },
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

                        if (viewModel.errorBitacora.isNotEmpty()) {
                            Text(viewModel.errorBitacora, color = MaterialTheme.colorScheme.error)
                        }

                        if (viewModel.exitoBitacora.isNotEmpty()) {
                            Text(viewModel.exitoBitacora, color = MaterialTheme.colorScheme.primary)
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

            VistaProfesor.BITACORAS_CURSO -> {
                HeaderProfesorSubpantalla(
                    titulo = "Ver Bitácoras por Curso",
                    onVolver = { vistaActual = VistaProfesor.MENU }
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownFiltroExpanded,
                        onExpandedChange = { dropdownFiltroExpanded = !dropdownFiltroExpanded }
                    ) {
                        OutlinedTextField(
                            value = cursoFiltroSeleccionado?.nombre ?: "Todos los Cursos",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filtrar por Curso") },
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
                                    cursoFiltroId = null
                                    dropdownFiltroExpanded = false
                                }
                            )
                            cursos.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.nombre} (${c.codigo})") },
                                    onClick = {
                                        cursoFiltroId = c.id
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
                                val entradasDeBitacora = remember(entradasEnviadas) {
                                    entradasEnviadas.filter { it.bitacoraId == b.id }
                                }

                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                                    text = "${entradasDeBitacora.size} Entradas",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        if (entradasDeBitacora.isNotEmpty()) {
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                            Text("Fotografías y Entradas del Alumno:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                            entradasDeBitacora.forEach { e ->
                                                val fotos = e.obtenerListaFotos()
                                                if (fotos.isNotEmpty()) {
                                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        items(fotos) { ruta ->
                                                            AsyncImage(
                                                                model = Uri.parse(ruta),
                                                                contentDescription = "Foto bitácora",
                                                                modifier = Modifier
                                                                    .size(120.dp)
                                                                    .clip(RoundedCornerShape(8.dp)),
                                                                contentScale = ContentScale.Crop
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
                    }
                }
            }

            VistaProfesor.REVISAR_ENTRADAS -> {
                HeaderProfesorSubpantalla(
                    titulo = "Bitácoras / Entradas a Revisar",
                    onVolver = { vistaActual = VistaProfesor.MENU }
                )

                if (entradasEnviadas.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "No hay entradas pendientes de revisión enviadas por alumnos.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(entradasEnviadas) { entrada ->
                            val listaFotos = entrada.obtenerListaFotos()

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

                                    if (listaFotos.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("📷 Fotografías adjuntas por el estudiante (${listaFotos.size}):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(listaFotos) { ruta ->
                                                AsyncImage(
                                                    model = Uri.parse(ruta),
                                                    contentDescription = "Imagen enviada por estudiante",
                                                    modifier = Modifier
                                                        .size(150.dp)
                                                        .clip(RoundedCornerShape(10.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
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
                                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
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

@Composable
fun OpcionMenuProfesorCard(
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
fun HeaderProfesorSubpantalla(
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
