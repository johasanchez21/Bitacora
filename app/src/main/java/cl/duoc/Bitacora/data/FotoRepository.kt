package cl.duoc.Bitacora.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class FotoRepository(
    private val context: Context
) {

    suspend fun guardarFoto(
        fotoUri: Uri
    ): String? = withContext(Dispatchers.IO) {

        try {

            val carpetaFotos = File(
                context.filesDir,
                "fotos_bitacora"
            )

            if (!carpetaFotos.exists()) {
                carpetaFotos.mkdirs()
            }

            val archivoDestino = File(
                carpetaFotos,
                "${UUID.randomUUID()}.jpg"
            )

            val entrada = context.contentResolver
                .openInputStream(fotoUri)
                ?: return@withContext null

            entrada.use { input ->
                archivoDestino.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Uri.fromFile(archivoDestino).toString()

        } catch (e: Exception) {
            null
        }
    }
}
