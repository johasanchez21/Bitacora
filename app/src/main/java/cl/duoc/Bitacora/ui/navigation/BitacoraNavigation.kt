package cl.duoc.Bitacora.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.Bitacora.domain.model.RolUsuario
import cl.duoc.Bitacora.ui.screens.estudiante.EstudianteScreen
import cl.duoc.Bitacora.ui.screens.login.LoginScreen
import cl.duoc.Bitacora.ui.screens.profesor.ProfesorScreen
import cl.duoc.Bitacora.ui.viewmodel.LoginViewModel

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
