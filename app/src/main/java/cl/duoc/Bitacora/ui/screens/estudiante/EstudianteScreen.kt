package cl.duoc.Bitacora.ui.screens.estudiante

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.domain.model.EstadoEntrada
import cl.duoc.Bitacora.domain.model.Usuario
import coil3.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    val bitacorasPendientes = remember(bitacoras, entradasCompletadas) {
        val idsCompletadas = entradasCompletadas.map { it.bitacoraId }.toSet()
        bitacoras.filter { it.id !in idsCompletadas }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
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
                            subtitulo = "${bitacorasPendientes.size} bitácoras por realizar (Estado: Vacía o En borrador)",
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

                // Formulario Desplazable de Entrada
                AnimatedVisibility(visible = mostrarFormulario) {
                    FormularioEntradaCard(
                        viewModel = viewModel,
                        usuarioEstudiante = usuarioEstudiante,
                        onCancelar = { mostrarFormulario = false },
                        onGuardarBorradorExito = {
                            Toast.makeText(context, "Borrador guardado", Toast.LENGTH_SHORT).show()
                            mostrarFormulario = false
                            vistaActual = VistaEstudiante.BORRADORES
                        },
                        onEnviarExito = {
                            Toast.makeText(context, "Bitácora enviada", Toast.LENGTH_LONG).show()
                            mostrarFormulario = false
                            vistaActual = VistaEstudiante.HISTORIAL
                        }
                    )
                }

                if (bitacorasPendientes.isEmpty()) {
                    MensajeVacio("No tienes bitácoras pendientes asignadas por tus profesores.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(bitacorasPendientes) { b ->
                            // ... resto del código de la tarjeta sin cambios
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

                // Formulario Desplazable
                AnimatedVisibility(visible = mostrarFormulario) {
                    FormularioEntradaCard(
                        viewModel = viewModel,
                        usuarioEstudiante = usuarioEstudiante,
                        onCancelar = { mostrarFormulario = false },
                        onGuardarBorradorExito = {
                            Toast.makeText(context, "Borrador guardado", Toast.LENGTH_SHORT).show()
                            mostrarFormulario = false
                            vistaActual = VistaEstudiante.BORRADORES
                        },
                        onEnviarExito = {
                            Toast.makeText(context, "Bitácora enviada", Toast.LENGTH_LONG).show()
                            mostrarFormulario = false
                            vistaActual = VistaEstudiante.HISTORIAL
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
                            val listaFotos = entrada.obtenerListaFotos()

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

                                    if (listaFotos.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Fotografías adjuntas (${listaFotos.size}):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(listaFotos) { ruta ->
                                                AsyncImage(
                                                    model = Uri.parse(ruta),
                                                    contentDescription = "Fotografía de bitácora",
                                                    modifier = Modifier
                                                        .size(140.dp)
                                                        .clip(RoundedCornerShape(10.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
                                    }

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
fun SelectorImagenConPermisos(
    cantidadActual: Int,
    onFotoSeleccionada: (Uri) -> Unit
) {
    val context = LocalContext.current
    var tempCamaraUri by remember { mutableStateOf<Uri?>(null) }
    var mensajePermiso by remember { mutableStateOf<String?>(null) }

    val puedeAgregar = cantidadActual < 3

    // Launcher Galería
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onFotoSeleccionada(uri)
        }
    }

    // Permiso Galería
    val permisoGaleria = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcherPermisoGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mensajePermiso = null
            launcherGaleria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            mensajePermiso = "Se requiere permiso de acceso a fotos/galería."
        }
    }

    // Launcher Cámara
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && tempCamaraUri != null) {
            onFotoSeleccionada(tempCamaraUri!!)
        }
    }

    // Permiso Cámara
    val launcherPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mensajePermiso = null
            val uri = crearArchivoTemporalCamara(context)
            tempCamaraUri = uri
            if (uri != null) {
                launcherCamara.launch(uri)
            }
        } else {
            mensajePermiso = "Se requiere permiso de la cámara para tomar fotografías."
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Adjuntar Fotografías (Galería o Cámara)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Surface(
                shape = RoundedCornerShape(50),
                color = if (puedeAgregar) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "$cantidadActual / 3 imágenes",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (puedeAgregar) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Text(
            text = "Puedes adjuntar un máximo de 3 imágenes a tu entrada (Galería o Cámara).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    if (!puedeAgregar) {
                        mensajePermiso = "Ya has alcanzado el límite máximo de 3 imágenes."
                        return@OutlinedButton
                    }
                    val tienePermiso = ContextCompat.checkSelfPermission(context, permisoGaleria) == PackageManager.PERMISSION_GRANTED
                    if (tienePermiso) {
                        mensajePermiso = null
                        launcherGaleria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        launcherPermisoGaleria.launch(permisoGaleria)
                    }
                },
                enabled = puedeAgregar,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Desde Galería")
            }

            OutlinedButton(
                onClick = {
                    if (!puedeAgregar) {
                        mensajePermiso = "Ya has alcanzado el límite máximo de 3 imágenes."
                        return@OutlinedButton
                    }
                    val tienePermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    if (tienePermiso) {
                        mensajePermiso = null
                        val uri = crearArchivoTemporalCamara(context)
                        tempCamaraUri = uri
                        if (uri != null) {
                            launcherCamara.launch(uri)
                        }
                    } else {
                        launcherPermisoCamara.launch(Manifest.permission.CAMERA)
                    }
                },
                enabled = puedeAgregar,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Usar Cámara")
            }
        }

        mensajePermiso?.let { msg ->
            Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun crearArchivoTemporalCamara(context: Context): Uri? {
    return try {
        val carpetaFotos = File(context.cacheDir, "camera_photos").apply { if (!exists()) mkdirs() }
        val archivoTemp = File.createTempFile("foto_camara_", ".jpg", carpetaFotos).apply {
            createNewFile()
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivoTemp
        )
    } catch (_: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioEntradaCard(
    viewModel: EstudianteViewModel,
    usuarioEstudiante: Usuario,
    onCancelar: () -> Unit,
    onGuardarBorradorExito: () -> Unit,
    onEnviarExito: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var mostrarDatePicker by rememberSaveable { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            val fechaFormateada = formatter.format(Date(millis))
                            viewModel.actualizarFecha(fechaFormateada)
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 580.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (viewModel.borradorIdSeleccionado != null) "Editar Borrador de Bitácora" else "Nueva Entrada de Bitácora",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onCancelar) {
                    Text("Cancelar")
                }
            }

            viewModel.bitacoraSeleccionada?.let { b ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Bitácora: ${b.asignatura} (${b.curso})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Campo de fecha de solo lectura con calendario nativo y bloqueo de fechas futuras
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = viewModel.fechaInput,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de Entrada (YYYY-MM-DD)") },
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Seleccionar fecha"
                            )
                        }
                    },
                    isError = viewModel.errorFecha.isNotEmpty(),
                    supportingText = {
                        if (viewModel.errorFecha.isNotEmpty()) {
                            Text(viewModel.errorFecha, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { mostrarDatePicker = true }
                )
            }

            // Actividad realizada (min 10 caracteres con letras, max 500)
            OutlinedTextField(
                value = viewModel.actividadInput,
                onValueChange = { viewModel.actualizarActividad(it) },
                label = { Text("Actividad Realizada (mínimo 10 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                isError = viewModel.errorActividad.isNotEmpty(),
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (viewModel.errorActividad.isNotEmpty()) {
                            Text(viewModel.errorActividad, color = MaterialTheme.colorScheme.error)
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Text("${viewModel.actividadInput.length}/500")
                    }
                }
            )

            // Aprendizaje obtenido (min 10 caracteres con letras, max 500)
            OutlinedTextField(
                value = viewModel.aprendizajeInput,
                onValueChange = { viewModel.actualizarAprendizaje(it) },
                label = { Text("¿Qué aprendiste hoy? (mínimo 10 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                isError = viewModel.errorAprendizaje.isNotEmpty(),
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (viewModel.errorAprendizaje.isNotEmpty()) {
                            Text(viewModel.errorAprendizaje, color = MaterialTheme.colorScheme.error)
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Text("${viewModel.aprendizajeInput.length}/500")
                    }
                }
            )

            // Dudas o consultas (opcional, si tiene texto 5-300 caracteres con letras)
            OutlinedTextField(
                value = viewModel.dudasInput,
                onValueChange = { viewModel.actualizarDudas(it) },
                label = { Text("Dudas o consultas (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 1,
                isError = viewModel.errorDudas.isNotEmpty(),
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (viewModel.errorDudas.isNotEmpty()) {
                            Text(viewModel.errorDudas, color = MaterialTheme.colorScheme.error)
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Text("${viewModel.dudasInput.length}/300")
                    }
                }
            )

            // Selector de Imagen (Galería o Cámara) con permisos y límite de 3
            SelectorImagenConPermisos(
                cantidadActual = viewModel.fotosUrisInput.size,
                onFotoSeleccionada = { uri ->
                    viewModel.agregarFotografia(uri, context)
                }
            )

            AnimatedVisibility(visible = viewModel.guardandoFoto) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Procesando imagen...", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Lista de imágenes adjuntas en el formulario con botón individual para eliminar
            if (viewModel.fotosUrisInput.isNotEmpty()) {
                Text(
                    text = "Imágenes Adjuntas (${viewModel.fotosUrisInput.size}/3):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                viewModel.fotosUrisInput.forEachIndexed { index, rutaFoto ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📷 Imagen ${index + 1}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = { viewModel.eliminarFotografia(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Foto", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                                }
                            }
                            AsyncImage(
                                model = Uri.parse(rutaFoto),
                                contentDescription = "Foto $index de entrada de bitácora",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            viewModel.errorFoto?.let { err ->
                Text(err, color = MaterialTheme.colorScheme.error)
            }

            if (viewModel.mensajeError.isNotEmpty()) {
                Text(viewModel.mensajeError, color = MaterialTheme.colorScheme.error)
            }

            if (viewModel.mensajeExito.isNotEmpty()) {
                Text(viewModel.mensajeExito, color = MaterialTheme.colorScheme.primary)
            }

            // Botón de Guardar en Borrador y Botón de Enviar Entrada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.guardarBorrador(usuarioEstudiante) {
                            onGuardarBorradorExito()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !viewModel.guardandoFoto,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Guardar en borrador")
                }

                Button(
                    onClick = {
                        viewModel.guardarYEnviarEntrada(usuarioEstudiante) {
                            onEnviarExito()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !viewModel.guardandoFoto,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Enviar")
                }
            }
        }
    }
}
