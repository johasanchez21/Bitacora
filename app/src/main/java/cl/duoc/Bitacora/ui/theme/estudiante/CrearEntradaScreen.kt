package cl.duoc.Bitacora.ui.theme.estudiante

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.viewmodel.EntradaBitacoraViewModel
import java.util.Calendar
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

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

    val selectorFecha = DatePickerDialog(
        context,
        { _, anio, mes, dia ->
            val fechaSeleccionada =
                "%02d/%02d/%04d".format(dia, mes + 1, anio)

            entradaViewModel.actualizarFecha(fechaSeleccionada)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        if (uri != null) {
            entradaViewModel.guardarFotografia(uri)
        }
    }

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
            text = "Crear entrada",
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
                    Text(entradaViewModel.errorActividad)
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
                    Text(entradaViewModel.errorAprendizaje)
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
                    Text(entradaViewModel.errorDudas)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Button(
            onClick = {
                selectorFoto.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar fotografía")
        }


        if (entradaViewModel.guardandoFoto) {
            Text("Guardando fotografía...")
        }


        entradaViewModel.fotoUri?.let { rutaFoto ->

            Text("Fotografía adjunta:")

            AsyncImage(
                model = Uri.parse(rutaFoto),
                contentDescription = "Fotografía adjunta a la entrada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            Text("Fotografía guardada correctamente")
        }


        entradaViewModel.errorFoto?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }


        Button(
            onClick = {
                entradaViewModel.guardarBorrador(
                    estudianteId = estudianteId,
                    bitacoraId = bitacoraId
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !entradaViewModel.guardandoFoto,
            ) {
            Text("Guardar borrador")
        }

        Button(
            onClick = {
                entradaViewModel.enviarEntrada(
                    estudianteId = estudianteId,
                    bitacoraId = bitacoraId
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !entradaViewModel.guardandoFoto,
        ) {
            Text("Enviar entrada")
        }

        if (entradaViewModel.mensajeResultado.isNotEmpty()) {
            Text(
                text = entradaViewModel.mensajeResultado,
                color = MaterialTheme.colorScheme.primary
            )
        }


        TextButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al menú")
        }
    }
}
