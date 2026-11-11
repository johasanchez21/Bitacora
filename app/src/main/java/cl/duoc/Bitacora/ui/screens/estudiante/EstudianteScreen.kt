package cl.duoc.Bitacora.ui.screens.estudiante

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
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.ui.viewmodel.EstudianteDestino
import cl.duoc.Bitacora.ui.viewmodel.EstudianteViewModel

@Composable
fun EstudianteScreen(
    nombre: String,
    estudianteId: Int,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EstudianteViewModel = viewModel()
) {
    when (val destino = viewModel.destinoActual) {
        is EstudianteDestino.MenuPrincipal -> {
            EstudianteMenuContent(
                nombre = nombre,
                onMisBitacorasClick = { viewModel.abrirMisBitacoras() },
                onCrearEntradaClick = { viewModel.abrirSeleccionBitacoraParaCrearEntrada() },
                onMiHistorialClick = { viewModel.abrirHistorial() },
                onCerrarSesion = onCerrarSesion,
                modifier = modifier
            )
        }

        is EstudianteDestino.MisBitacoras -> {
            MisBitacorasScreen(
                onVolver = { viewModel.volverAMenu() },
                onSeleccionarBitacora = { bitacoraId ->
                    viewModel.seleccionarBitacora(bitacoraId)
                },
                modifier = modifier
            )
        }

        is EstudianteDestino.DetalleBitacora -> {
            DetalleBitacoraScreen(
                bitacoraId = destino.bitacoraId,
                onVolver = { viewModel.volverAMisBitacoras() },
                modifier = modifier
            )
        }

        is EstudianteDestino.CrearEntrada -> {
            CrearEntradaScreen(
                estudianteId = estudianteId,
                bitacoraId = destino.bitacoraId,
                borradorId = destino.borradorId,
                onVolver = { viewModel.volverAMenu() },
                modifier = modifier
            )
        }

        is EstudianteDestino.Historial -> {
            HistorialEntradasScreen(
                estudianteId = estudianteId,
                onVolver = { viewModel.volverAMenu() },
                onEditarBorrador = { entradaId, bitacoraId ->
                    viewModel.editarBorrador(entradaId = entradaId, bitacoraId = bitacoraId)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun EstudianteMenuContent(
    nombre: String,
    onMisBitacorasClick: () -> Unit,
    onCrearEntradaClick: () -> Unit,
    onMiHistorialClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            onClick = onMisBitacorasClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis bitácoras")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCrearEntradaClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear entrada")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onMiHistorialClick,
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
