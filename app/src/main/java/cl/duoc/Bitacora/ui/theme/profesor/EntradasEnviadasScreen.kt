package cl.duoc.Bitacora.ui.theme.profesor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.viewmodel.EntradasEnviadasViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.net.Uri
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun EntradasEnviadasScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    entradasViewModel: EntradasEnviadasViewModel = viewModel()
) {

    val entradas = entradasViewModel.entradasEnviadas
        .collectAsState()
        .value

    var entradaSeleccionadaId by remember {
        mutableStateOf<Int?>(null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Entradas pendientes de revisión",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Consulta las entradas enviadas por los estudiantes"
        )

        TextButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al menú")
        }

        if (entradas.isEmpty()) {

            Text("No hay entradas pendientes de revisión")

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = entradas,
                    key = { entrada -> entrada.id }
                ) { entrada ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = "Entrada #${entrada.id}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Estudiante ID: ${entrada.estudianteId}"
                            )

                            Text(
                                text = "Bitácora ID: ${entrada.bitacoraId}"
                            )

                            Text(
                                text = "Fecha: ${entrada.fecha}"
                            )

                            Text(
                                text = "Actividad: ${entrada.actividad}"
                            )

                            Text(
                                text = "Aprendizaje: ${entrada.aprendizaje}"
                            )

                            if (entrada.dudas.isNotBlank()) {
                                Text(
                                    text = "Dudas: ${entrada.dudas}"
                                )
                            }

                            Text(
                                text = "Estado: Enviada"
                            )

                            entrada.fotoUri?.let { rutaFoto ->

                                Text(
                                    text = "Fotografía adjunta:",
                                    style = MaterialTheme.typography.titleSmall
                                )

                                AsyncImage(
                                    model = Uri.parse(rutaFoto),
                                    contentDescription = "Fotografía enviada por el estudiante",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            if (entradaSeleccionadaId == entrada.id) {

                                OutlinedTextField(
                                    value = entradasViewModel.comentarioProfesor,
                                    onValueChange = {
                                        entradasViewModel.actualizarComentario(it)
                                    },
                                    label = {
                                        Text("Comentario del profesor")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    maxLines = 5
                                )

                                Button(
                                    onClick = {
                                        entradasViewModel.revisarEntrada(entrada.id)
                                    },
                                    enabled = !entradasViewModel.revisandoEntrada,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Marcar como revisada")
                                }

                                if (entradasViewModel.mensajeResultado.isNotBlank()) {
                                    Text(
                                        text = entradasViewModel.mensajeResultado
                                    )
                                }

                            } else {

                                TextButton(
                                    onClick = {
                                        entradaSeleccionadaId = entrada.id
                                        entradasViewModel.actualizarComentario("")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Revisar esta entrada")
                                }
                            }

                        }
                    }
                }
            }
        }
    }


}

