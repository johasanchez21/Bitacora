package cl.duoc.Bitacora.ui.theme.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.viewmodel.LoginViewModel
import cl.duoc.Bitacora.validation.LoginValidator


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onIrARegistro: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel()
)  {

    var mostrarPassword by remember { mutableStateOf(false) }

    var correo = loginViewModel.correo
    var password = loginViewModel.password

    var errorCorreo = loginViewModel.errorCorreo
    var errorPassword = loginViewModel.errorPassword

    val errorLogin = loginViewModel.errorLogin

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bitácora",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu espacio de aprendizaje",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))


        OutlinedTextField(
            value = correo,
            onValueChange = {
                loginViewModel.actualiazrCorreo(it)
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorCorreo.isNotEmpty(),
            supportingText = {
                if (errorCorreo.isNotEmpty()) {
                    Text(errorCorreo)
                }
            }
        )


        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = password,
            onValueChange = {
                loginViewModel.actualizarPassword(it)
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorPassword.isNotEmpty(),
            supportingText = {
                if (errorPassword.isNotEmpty()) {
                    Text(errorPassword)
                }
            },
            visualTransformation = if (mostrarPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        mostrarPassword = !mostrarPassword
                    }
                ) {
                    Icon(
                        imageVector = if (mostrarPassword) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = "Mostrar u ocultar contraseña"
                    )
                }
            }
        )


        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {
                if (loginViewModel.iniciarSesion()) {
                    println("Inicio de sesion correcto")

                    println("Usuario: ${loginViewModel.usuarioActual?.nombre}")
                    println("Rol: ${loginViewModel.usuarioActual?.rol}")
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
        if (errorLogin.isNotEmpty()){
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = errorLogin,
                color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onIrARegistro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar estudiante o profesor")
        }
    }

}
