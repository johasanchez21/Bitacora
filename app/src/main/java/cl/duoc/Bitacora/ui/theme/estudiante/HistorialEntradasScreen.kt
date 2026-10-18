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

@Composable
fun HistorialEntradasScreen(
    estudianteId: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

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

        if (entradas.isEmpty()) {

            Text("Todavía no tienes entradas registradas")

        } else {

            entradas.forEach { entrada ->

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
