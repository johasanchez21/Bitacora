package cl.duoc.Bitacora.ui.screens.estudiante

import android.app.DatePickerDialog
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.ui.viewmodel.EntradaBitacoraViewModel
import cl.duoc.Bitacora.ui.viewmodel.EntradaUiState
import coil3.compose.AsyncImage
import java.util.Calendar

@Composable
fun CrearEntradaScreen(
    estudianteId: Int,
    bitacoraId: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    borradorId: Int? = null,
    entradaViewModel: EntradaBitacoraViewModel = viewModel()
) {
    val context = LocalContext.current
    val calendario = remember { Calendar.getInstance() }
    val uiState by entradaViewModel.uiState.collectAsState()

    val selectorFecha = DatePickerDialog(
        context,
        { _, anio, mes, dia ->
            val fechaSeleccionada =
                "%04d-%02d-%02d".format(anio, mes + 1, dia)

            entradaViewModel.actualizarFecha(fechaSeleccionada)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(borradorId, estudianteId, bitacoraId) {
        if (borradorId != null) {
            entradaViewModel.cargarBorrador(
                entradaId = borradorId,
                estudianteId = estudianteId,
                bitacoraId = bitacoraId
            )
        } else {
            entradaViewModel.limpiarFormulario()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Crear entrada de bitácora",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Registra las actividades y aprendizajes de tu bitácora")

        OutlinedButton(
            onClick = { selectorFecha.show() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (entradaViewModel.fecha.isEmpty())
                    "Seleccionar fecha"
                else
                    "Fecha: ${entradaViewModel.fecha}"
            )
        }

        if (entradaViewModel.errorFecha.isNotEmpty()) {
            Text(
                text = entradaViewModel.errorFecha,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = entradaViewModel.actividad,
            onValueChange = entradaViewModel::actualizarActividad,
            label = { Text("Actividad realizada") },
            isError = entradaViewModel.errorActividad.isNotEmpty(),
            supportingText = {
                if (entradaViewModel.errorActividad.isNotEmpty()) {
                    Text(entradaViewModel.errorActividad, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = entradaViewModel.aprendizaje,
            onValueChange = entradaViewModel::actualizarAprendizaje,
            label = { Text("¿Qué aprendiste?") },
            isError = entradaViewModel.errorAprendizaje.isNotEmpty(),
            supportingText = {
                if (entradaViewModel.errorAprendizaje.isNotEmpty()) {
                    Text(entradaViewModel.errorAprendizaje, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = entradaViewModel.dudas,
            onValueChange = entradaViewModel::actualizarDudas,
            label = { Text("Dudas o dificultades (opcional)") },
            isError = entradaViewModel.errorDudas.isNotEmpty(),
            supportingText = {
                if (entradaViewModel.errorDudas.isNotEmpty()) {
                    Text(entradaViewModel.errorDudas, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        // Selector con Permisos para Galería o Cámara (Máximo 3 imágenes)
        SelectorImagenConPermisos(
            cantidadActual = entradaViewModel.fotosUris.size,
            onFotoSeleccionada = { uri ->
                entradaViewModel.guardarFotografia(uri)
            }
        )

        AnimatedVisibility(visible = entradaViewModel.guardandoFoto || uiState is EntradaUiState.Loading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Procesando imagen...", style = MaterialTheme.typography.bodySmall)
            }
        }

        if (entradaViewModel.fotosUris.isNotEmpty()) {
            Text(
                text = "Fotografías adjuntas (${entradaViewModel.fotosUris.size}/3):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            entradaViewModel.fotosUris.forEachIndexed { index, rutaFoto ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📷 Imagen ${index + 1}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { entradaViewModel.eliminarFotografia(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Foto", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        AsyncImage(
                            model = Uri.parse(rutaFoto),
                            contentDescription = "Fotografía $index adjunta",
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

        entradaViewModel.errorFoto?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    entradaViewModel.guardarBorrador(
                        estudianteId = estudianteId,
                        bitacoraId = bitacoraId
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = !entradaViewModel.guardandoFoto,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Guardar borrador")
            }

            Button(
                onClick = {
                    entradaViewModel.enviarEntrada(
                        estudianteId = estudianteId,
                        bitacoraId = bitacoraId
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = !entradaViewModel.guardandoFoto,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Enviar entrada")
            }
        }

        AnimatedVisibility(visible = entradaViewModel.mensajeResultado.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = if (uiState is EntradaUiState.Error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = entradaViewModel.mensajeResultado,
                    color = if (uiState is EntradaUiState.Error) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        TextButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al menú")
        }
    }
}
