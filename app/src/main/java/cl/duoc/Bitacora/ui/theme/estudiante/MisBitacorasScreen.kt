package cl.duoc.Bitacora.ui.theme.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.viewmodel.BitacoraViewModel

@Composable
fun MisBitacorasScreen(
    onVolver: () -> Unit,
    onSeleccionarBitacora: (Int) -> Unit,
    modifier: Modifier = Modifier,
    bitacoraViewModel: BitacoraViewModel = viewModel()
){
    LaunchedEffect(Unit) {
        bitacoraViewModel.cargarBitacoras("7° Básico A")
    }

    val bitacoras = bitacoraViewModel.bitacoras

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Mis bitácoras",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Consulta las bitácoras mensuales de tus asignaturas"
        )

        if (bitacoras.isEmpty()) {

            Text("No hay bitácoras publicadas disponibles")

        } else {

            bitacoras.forEach { bitacora ->

                BitacoraCard(
                    asignatura = bitacora.asignatura,
                    mes = "${bitacora.mes} ${bitacora.anio}",
                    estado = when (bitacora.estado) {
                        cl.duoc.Bitacora.model.EstadoBitacora.BORRADOR -> "Borrador"
                        cl.duoc.Bitacora.model.EstadoBitacora.PUBLICADA -> "Publicada"
                        cl.duoc.Bitacora.model.EstadoBitacora.CERRADA -> "Cerrada"

                    },
                    onVerBitacora = {
                        onSeleccionarBitacora(bitacora.id)
                    }
                )
            }
        }

        TextButton(onClick = onVolver) {
            Text("Volver al menú")
        }
    }
}

@Composable
fun BitacoraCard(
    asignatura: String,
    mes: String,
    estado: String,
    onVerBitacora: () -> Unit
)  {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = asignatura,
                style = MaterialTheme.typography.titleLarge
            )

            Text("Mes: $mes")

            Text("Estado: $estado")

            Button(
                onClick = onVerBitacora
            ) {
                Text("Ver bitácora")
            }
        }
    }
}

