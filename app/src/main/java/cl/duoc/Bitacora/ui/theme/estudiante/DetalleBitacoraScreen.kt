package cl.duoc.Bitacora.ui.theme.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import cl.duoc.Bitacora.data.BitacoraRepository
import cl.duoc.Bitacora.data.local.BitacoraDatabase

@Composable
fun DetalleBitacoraScreen(
    bitacoraId: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val repository = remember(context) {
        BitacoraRepository(
            BitacoraDatabase.obtenerInstancia(context).bitacoraDao()
        )
    }

    val flujoBitacoras = remember(repository) {
        repository.obtenerBitacorasPublicadasRoom("7° Básico A")
    }

    val bitacoras = flujoBitacoras.collectAsState(
        initial = emptyList()
    ).value

    val bitacora = bitacoras.find { it.id == bitacoraId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Detalle de bitácora",
            style = MaterialTheme.typography.headlineMedium
        )

        TextButton(onClick = onVolver) {
            Text("Volver a mis bitácoras")
        }

        if (bitacora == null) {
            Text("No se encontró la bitácora seleccionada")
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = bitacora.asignatura,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text("Curso: ${bitacora.curso}")
                    Text("Mes: ${bitacora.mes}")
                    Text("Año: ${bitacora.anio}")
                    Text("Estado: Publicada")
                }
            }
        }
    }
}
