package cl.duoc.Bitacora.ui.screens.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.domain.model.RolUsuario

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {},
    viewModel: RegistroViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.actualizarNombre(it) },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.correo,
            onValueChange = { viewModel.actualizarCorreo(it) },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.actualizarPassword(it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Rol del Usuario",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = viewModel.rol == RolUsuario.Estudiante,
                onClick = { viewModel.actualizarRol(RolUsuario.Estudiante) }
            )
            Text("Estudiante")

            Spacer(modifier = Modifier.padding(horizontal = 8.dp))

            RadioButton(
                selected = viewModel.rol == RolUsuario.Profesor,
                onClick = { viewModel.actualizarRol(RolUsuario.Profesor) }
            )
            Text("Profesor")
        }

        if (viewModel.rol == RolUsuario.Estudiante) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Seleccionar curso (Mínimo 1):",
                style = MaterialTheme.typography.titleSmall
            )

            // Simulación de cursos disponibles registrados
            listOf(1 to "1° Medio A - 2025", 2 to "2° Medio B - 2025").forEach { (id, nombre) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = viewModel.cursosSeleccionados.contains(id),
                        onCheckedChange = { viewModel.toggleSeleccionCurso(id) }
                    )
                    Text(nombre)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.mensajeError.isNotEmpty()) {
            Text(
                text = viewModel.mensajeError,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (viewModel.mensajeExito.isNotEmpty()) {
            Text(
                text = viewModel.mensajeExito,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.registrar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Login")
        }
    }
}
