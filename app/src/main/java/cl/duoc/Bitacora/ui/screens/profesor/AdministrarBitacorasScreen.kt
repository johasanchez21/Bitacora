package cl.duoc.Bitacora.ui.screens.profesor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Class
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.domain.model.EstadoBitacora
import cl.duoc.Bitacora.ui.viewmodel.AdministrarBitacorasViewModel

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Administrar bitácoras",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onVolver) {
                Text("Volver")
            }
        }

        Button(
            onClick = {
                mostrarFormulario = !mostrarFormulario
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                if (mostrarFormulario) {
                    "Ocultar formulario"
                } else {
                    "Crear nueva bitácora"
                }
            )
        }

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
                    Text(
                        text = administrarViewModel.mensajeResultado,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (bitacoras.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Todavía no hay bitácoras guardadas",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(
                items = bitacoras,
                key = { it.id }
            ) { bitacora ->

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Book,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = bitacora.asignatura,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ID: #${bitacora.id}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = when (bitacora.estado) {
                                    EstadoBitacora.PUBLICADA -> MaterialTheme.colorScheme.primaryContainer
                                    EstadoBitacora.BORRADOR -> MaterialTheme.colorScheme.secondaryContainer
                                    EstadoBitacora.CERRADA -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = bitacora.estado.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when (bitacora.estado) {
                                        EstadoBitacora.PUBLICADA -> MaterialTheme.colorScheme.onPrimaryContainer
                                        EstadoBitacora.BORRADOR -> MaterialTheme.colorScheme.onSecondaryContainer
                                        EstadoBitacora.CERRADA -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Class,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = bitacora.curso,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${bitacora.mes} ${bitacora.anio}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (bitacora.estado == EstadoBitacora.BORRADOR) {
                            Button(
                                onClick = {
                                    administrarViewModel.publicarBitacora(bitacora.id)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
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
