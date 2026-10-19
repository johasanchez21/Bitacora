package cl.duoc.Bitacora.ui.theme.profesor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.model.EstadoBitacora
import cl.duoc.Bitacora.viewmodel.AdministrarBitacorasViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically

@Composable
fun AdministrarBitacorasScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    administrarViewModel: AdministrarBitacorasViewModel = viewModel()
) {

    val bitacoras = administrarViewModel.bitacoras
        .collectAsState().value

    var asignatura by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("2026") }
    var curso by remember { mutableStateOf("7° Básico A") }
    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(text = "Administrar bitácoras")

        TextButton(onClick = onVolver) {
            Text("Volver")
        }

        Button(
            onClick = {
                mostrarFormulario = !mostrarFormulario
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (mostrarFormulario) {
                    "Ocultar formulario"
                } else {
                    "Crear nueva bitácora"
                }
            )
        }

        Text("Crear nueva bitácora")

        AnimatedVisibility(
            visible = mostrarFormulario,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = asignatura,
                    onValueChange = { asignatura = it },
                    label = { Text("Asignatura") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = administrarViewModel.errorAsignatura != null,
                    supportingText = {
                        administrarViewModel.errorAsignatura?.let {
                            Text(it)
                        }
                    }
                )

                OutlinedTextField(
                    value = mes,
                    onValueChange = { mes = it },
                    label = { Text("Mes") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = administrarViewModel.errorMes != null,
                    supportingText = {
                        administrarViewModel.errorMes?.let {
                            Text(it)
                        }
                    }
                )

                OutlinedTextField(
                    value = anio,
                    onValueChange = { anio = it },
                    label = { Text("Año") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = administrarViewModel.errorAnio != null,
                    supportingText = {
                        administrarViewModel.errorAnio?.let {
                            Text(it)
                        }
                    }
                )

                OutlinedTextField(
                    value = curso,
                    onValueChange = { curso = it },
                    label = { Text("Curso") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = administrarViewModel.errorCurso != null,
                    supportingText = {
                        administrarViewModel.errorCurso?.let {
                            Text(it)
                        }
                    }
                )

                Button(
                    onClick = {
                        administrarViewModel.crearBitacora(
                            asignatura = asignatura,
                            mes = mes,
                            anio = anio,
                            curso = curso
                        )
                    },
                    enabled = !administrarViewModel.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar bitácora")
                }

                if (administrarViewModel.mensajeResultado.isNotBlank()) {
                    Text(administrarViewModel.mensajeResultado)
                }
            }
        }
        if (bitacoras.isEmpty()) {
            Text("Todavía no hay bitácoras guardadas")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(
                items = bitacoras,
                key = { it.id }
            ) { bitacora ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Asignatura: ${bitacora.asignatura}"
                        )

                        Text(
                            text = "ID de la bitácora: ${bitacora.id}"
                        )

                        Text(
                            text = "Curso: ${bitacora.curso}"
                        )

                        Text(
                            text = "Mes: ${bitacora.mes} ${bitacora.anio}"
                        )

                        Text(
                            text = "Estado: ${bitacora.estado}"
                        )

                        if (
                            bitacora.estado ==
                            EstadoBitacora.BORRADOR
                        ) {

                            Button(
                                onClick = {
                                    administrarViewModel
                                        .publicarBitacora(bitacora.id)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Publicar bitácora")
                            }
                        }
                    }
                }
            }
        }
    }
}
