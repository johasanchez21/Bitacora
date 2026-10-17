package cl.duoc.Bitacora.ui.theme.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun EstudianteScreen(
    nombre: String,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarBitacoras by remember { mutableStateOf(false) }
    var mostrarCrearEntrada by remember { mutableStateOf(false) }

    if (mostrarBitacoras) {
        MisBitacorasScreen(
            onVolver = {
                mostrarBitacoras = false
            },
            modifier = modifier
        )
        return
    }
    if (mostrarCrearEntrada) {
        CrearEntradaScreen(
            onVolver = {
                mostrarCrearEntrada = false
            },
            modifier = modifier
        )
        return
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bienvenido, $nombre",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Panel del estudiante",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                mostrarBitacoras = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis bitácoras")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                mostrarCrearEntrada = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear entrada")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Próximamente: historial
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mi historial")
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
