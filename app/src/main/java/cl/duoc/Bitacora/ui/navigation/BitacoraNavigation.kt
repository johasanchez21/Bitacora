package cl.duoc.Bitacora.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.domain.model.Usuario
import cl.duoc.Bitacora.ui.screens.estudiante.EstudianteScreen
import cl.duoc.Bitacora.ui.screens.login.LoginScreen
import cl.duoc.Bitacora.ui.screens.profesor.ProfesorScreen

enum class DestinoNavegacion {
    LOGIN,
    ESTUDIANTE,
    PROFESOR
}

@Composable
fun BitacoraNavigation(
    modifier: Modifier = Modifier,
    onIrARegistro: () -> Unit = {}
) {
    var destinoActual by remember { mutableStateOf(DestinoNavegacion.LOGIN) }
    var usuarioLogueado by remember { mutableStateOf<Usuario?>(null) }

    when (destinoActual) {
        DestinoNavegacion.LOGIN -> {
            LoginScreen(
                modifier = modifier,
                onIrARegistro = onIrARegistro,
                onLoginExitoso = { usuario ->
                    usuarioLogueado = usuario
                    destinoActual = if (usuario.rol == RolUsuario.Profesor) {
                        DestinoNavegacion.PROFESOR
                    } else {
                        DestinoNavegacion.ESTUDIANTE
                    }
                }
            )
        }
        DestinoNavegacion.ESTUDIANTE -> {
            usuarioLogueado?.let { usuario ->
                EstudianteScreen(
                    usuarioEstudiante = usuario,
                    onCerrarSesion = {
                        usuarioLogueado = null
                        destinoActual = DestinoNavegacion.LOGIN
                    },
                    modifier = modifier
                )
            }
        }
        DestinoNavegacion.PROFESOR -> {
            usuarioLogueado?.let { usuario ->
                ProfesorScreen(
                    usuarioProfesor = usuario,
                    onCerrarSesion = {
                        usuarioLogueado = null
                        destinoActual = DestinoNavegacion.LOGIN
                    },
                    modifier = modifier
                )
            }
        }
    }
}
