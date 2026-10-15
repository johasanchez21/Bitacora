package cl.duoc.Bitacora.ui.theme.registros

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon

data class Persona(
    val tipo: String,
    val nombre: String,
    val correo: String,
    val identificador: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onVolver: () -> Unit
) {


    var tipo by remember { mutableStateOf("Estudiante") }
    var menuAbierto by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var identificador by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorId by remember { mutableStateOf<String?>(null) }
    var mensaje by remember { mutableStateOf("") }

    val personas = remember { mutableStateListOf<Persona>() }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text("Registro de personas")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al inicio de sesión"
                        )
                    }
                }
            )
        }


    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Registro de personas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            ExposedDropdownMenuBox(
                expanded = menuAbierto,
                onExpandedChange = { menuAbierto = it }
            ) {
                OutlinedTextField(
                    value = tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de persona") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(menuAbierto)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryNotEditable

                        )
                )

                ExposedDropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    listOf("Estudiante", "Profesor").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                tipo = opcion
                                menuAbierto = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorNombre = null
                    mensaje = ""
                },
                label = { Text("Nombre y apellido") },
                isError = errorNombre != null,
                supportingText = {
                    errorNombre?.let { Text(it) }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorCorreo = null
                    mensaje = ""
                },
                label = { Text("Correo electrónico") },
                isError = errorCorreo != null,
                supportingText = {
                    errorCorreo?.let { Text(it) }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = identificador,
                onValueChange = {
                    identificador = it
                    errorId = null
                    mensaje = ""
                },
                label = { Text("RUT o identificador institucional") },
                isError = errorId != null,
                supportingText = {
                    errorId?.let { Text(it) }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {

                    errorNombre = if (nombre.isBlank()) {
                        "Ingresa el nombre."
                    } else null

                    errorCorreo = if (
                        !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
                    ) {
                        "Ingresa un correo válido."
                    } else null

                    errorId = when {
                        identificador.isBlank() ->
                            "Ingresa un identificador."

                        personas.any {
                            it.identificador.equals(
                                identificador.trim(),
                                ignoreCase = true
                            )
                        } -> "El identificador ya existe."

                        else -> null
                    }

                    if (
                        errorNombre == null &&
                        errorCorreo == null &&
                        errorId == null
                    ) {
                        personas.add(
                            Persona(
                                tipo,
                                nombre.trim(),
                                correo.trim(),
                                identificador.trim()
                            )
                        )

                        mensaje = "$tipo registrado correctamente."

                        nombre = ""
                        correo = ""
                        identificador = ""
                    } else {
                        mensaje = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar registro")
            }

            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider()

            Text(
                text = "Personas registradas: ${personas.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = personas,
                    key = { it.identificador }
                ) { persona ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                persona.tipo,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                persona.nombre,
                                fontWeight = FontWeight.Bold
                            )
                            Text(persona.correo)
                            Text("ID: ${persona.identificador}")
                        }
                    }
                }
            }
        }
    }
}
