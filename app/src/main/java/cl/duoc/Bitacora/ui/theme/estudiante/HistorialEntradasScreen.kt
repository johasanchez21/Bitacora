package cl.duoc.Bitacora.ui.theme.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.Bitacora.data.EntradaBitacoraRepository
import cl.duoc.Bitacora.data.local.BitacoraDatabase
import cl.duoc.Bitacora.model.EstadoEntrada
import android.net.Uri
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.material3.Button
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.viewmodel.PdfViewModel
import android.content.Intent
import android.content.ActivityNotFoundException
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.duoc.Bitacora.data.BitacoraRepository
import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun HistorialEntradasScreen(
    estudianteId: Int,
    onVolver: () -> Unit,
    onEditarBorrador: (Int, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val pdfViewModel: PdfViewModel = viewModel()

    val context = LocalContext.current


    val bitacoraRepository = remember(context) {
        BitacoraRepository(
            BitacoraDatabase.obtenerInstancia(context).bitacoraDao()
        )
    }

    val flujoBitacoras = remember(bitacoraRepository) {
        bitacoraRepository.obtenerBitacorasPublicadasRoom("7° Básico A")
    }

    val bitacoras = flujoBitacoras.collectAsState(
        initial = emptyList()
    ).value

    var bitacoraSeleccionadaId by remember {
        mutableStateOf<Int?>(null)
    }


    val repository = remember(context) {
        val dao = BitacoraDatabase
            .obtenerInstancia(context)
            .entradaBitacoraDao()

        EntradaBitacoraRepository(dao)
    }

    val flujoEntradas = remember(repository, estudianteId) {
        repository.obtenerEntradasPorEstudiante(estudianteId)
    }

    val entradas = flujoEntradas.collectAsState(
        initial = emptyList()
    ).value

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Mi historial",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Consulta las entradas que has registrado")

        Text(
            text = "Selecciona la bitácora para el informe",
            style = MaterialTheme.typography.titleMedium
        )

        bitacoras.forEach { bitacora ->

            OutlinedButton(
                onClick = {
                    bitacoraSeleccionadaId = bitacora.id
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (bitacoraSeleccionadaId == bitacora.id) {
                        "✓ ${bitacora.asignatura} - ${bitacora.mes} ${bitacora.anio}"
                    } else {
                        "${bitacora.asignatura} - ${bitacora.mes} ${bitacora.anio}"
                    }
                )
            }
        }

        Button(
            onClick = {
                bitacoraSeleccionadaId?.let { id ->
                    pdfViewModel.generarInformeMensual(
                        estudianteId = estudianteId,
                        bitacoraId = id
                    )
                }
            },
            enabled = bitacoraSeleccionadaId != null &&
                    !pdfViewModel.generandoPdf,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (pdfViewModel.generandoPdf) {
                    "Generando informe..."
                } else {
                    "Generar informe mensual"
                }
            )
        }

        if (pdfViewModel.mensajeResultado.isNotBlank()) {
            Text(
                text = pdfViewModel.mensajeResultado,
                color = if (pdfViewModel.rutaPdf != null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        pdfViewModel.rutaPdf?.let { ruta ->

            Button(
                onClick = {

                    val archivoPdf = File(ruta)

                    if (archivoPdf.exists()) {

                        val uriPdf = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            archivoPdf
                        )

                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uriPdf, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        try {
                            context.startActivity(
                                Intent.createChooser(
                                    intent,
                                    "Abrir PDF con"
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            android.widget.Toast.makeText(
                                context,
                                "No hay un visor de PDF disponible",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir PDF")
            }
        }



        if (entradas.isEmpty()) {

            Text("Todavía no tienes entradas registradas")

        } else {

            entradas.forEach { entrada ->

                var expandida by remember(entrada.id) {
                    mutableStateOf(false)
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxWidth()
                        .animateContentSize()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Entrada #${entrada.id}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextButton(
                            onClick = {
                                expandida = !expandida
                            }
                        ) {
                            Text(
                                if (expandida) {
                                    "Ocultar detalles"
                                } else {
                                    "Ver detalles"
                                }
                            )
                        }
                        if (expandida) {
                        Text(
                            "Fecha: ${
                                entrada.fecha.ifBlank { "Sin fecha" }
                            }"
                        )

                        Text(
                            "Actividad: ${
                                entrada.actividad.ifBlank { "Sin completar" }
                            }"
                        )

                        Text(
                            "Aprendizaje: ${
                                entrada.aprendizaje.ifBlank { "Sin completar" }
                            }"
                        )

                        Text(
                            "Estado: ${
                                when (entrada.estado) {
                                    EstadoEntrada.BORRADOR -> "Borrador"
                                    EstadoEntrada.ENVIADA -> "Enviada"
                                    EstadoEntrada.REVISADA -> "Revisada"
                                }
                            }"
                        )

                        if (entrada.comentarioProfesor.isNotBlank()) {
                            Text(
                                "Comentario del profesor: ${entrada.comentarioProfesor}"
                            )
                        }

                        entrada.fotoUri?.let { rutaFoto ->

                            Text(
                                text = "Fotografía adjunta:",
                                style = MaterialTheme.typography.titleSmall
                            )

                            AsyncImage(
                                model = Uri.parse(rutaFoto),
                                contentDescription = "Fotografía adjunta a la entrada",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        if (entrada.estado == EstadoEntrada.BORRADOR) {

                            TextButton(
                                onClick = {
                                    onEditarBorrador(
                                        entrada.id,
                                        entrada.bitacoraId
                                    )
                                }
                            ) {
                                Text("Editar borrador")
                            }
                        }
                        }
                    }
                }
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
