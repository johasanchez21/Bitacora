package cl.duoc.Bitacora.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.model.RolUsuario
import cl.duoc.Bitacora.ui.theme.estudiante.EstudianteScreen
import cl.duoc.Bitacora.ui.theme.login.LoginScreen
import cl.duoc.Bitacora.ui.theme.profesor.ProfesorScreen
import cl.duoc.Bitacora.viewmodel.LoginViewModel

@Composable
fun BitacoraNavigation(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(),
    onIrARegistro: () -> Unit = {}
) {

    val usuario = loginViewModel.usuarioActual

    when (usuario?.rol) {

        RolUsuario.Estudiante -> {

            key(usuario.id, usuario.rol) {
                EstudianteScreen(
                    nombre = usuario.nombre,
                    estudianteId = usuario.id,
                    onCerrarSesion = {
                        loginViewModel.cerrarSesion()
                    }
                )
            }
        }

        RolUsuario.Profesor -> {

            key(usuario.id, usuario.rol) {
                ProfesorScreen(
                    nombre = usuario.nombre,
                    onCerrarSesion = {
                        loginViewModel.cerrarSesion()
                    },
                    modifier = modifier
                )
            }
        }

        null -> {

            LoginScreen(
                modifier = modifier,
                loginViewModel = loginViewModel,
                onIrARegistro = onIrARegistro
            )
        }
    }
}
