package cl.duoc.Bitacora.ui.theme.profesor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button

@Composable
fun ProfesorScreen(
    nombre: String,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {

    var mostrarEntradas by remember {
        mutableStateOf(false)
    }

    var mostrarAdministrarBitacoras by remember {
        mutableStateOf(false)
    }


    if (mostrarEntradas) {
        EntradasEnviadasScreen(
            onVolver = {
                mostrarEntradas = false
            },
            modifier = modifier
        )
        return
    }


    if (mostrarAdministrarBitacoras) {

        AdministrarBitacorasScreen(
            onVolver = {
                mostrarAdministrarBitacoras = false
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

        Spacer(modifier = Modifier.height(12.dp))

        Text("Panel del profesor")

        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {
                mostrarEntradas = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Revisar entradas")
        }


        Button(
            onClick = {
                mostrarAdministrarBitacoras = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Administrar bitácoras")
        }


        Button(onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
