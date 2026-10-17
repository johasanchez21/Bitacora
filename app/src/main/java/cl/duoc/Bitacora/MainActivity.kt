package cl.duoc.Bitacora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cl.duoc.Bitacora.ui.theme.BitacoraTheme
import cl.duoc.Bitacora.ui.theme.login.LoginScreen
import cl.duoc.Bitacora.ui.theme.registros.RegistroScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.duoc.Bitacora.navigation.BitacoraNavigation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BitacoraTheme {

                var mostrarRegistro by remember {
                    mutableStateOf(false)
                }

                if (mostrarRegistro) {
                    RegistroScreen(
                        onVolver = {
                            mostrarRegistro = false
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->

                        BitacoraNavigation(
                            modifier = Modifier.padding(innerPadding),
                            onIrARegistro = {
                                mostrarRegistro = true
                            }
                        )
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    BitacoraTheme {
        LoginScreen()
    }
}