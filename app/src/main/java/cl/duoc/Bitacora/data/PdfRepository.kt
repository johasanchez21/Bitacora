package cl.duoc.Bitacora.data

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.util.UUID
import cl.duoc.Bitacora.model.Bitacora
import cl.duoc.Bitacora.model.EntradaBitacora

class PdfRepository(
    private val context: Context
) {

    fun generarPdfPrueba(): File {

        val documento = PdfDocument()

        try {
            val paginaInfo = PdfDocument.PageInfo.Builder(
                595,
                842,
                1
            ).create()

            val pagina = documento.startPage(paginaInfo)

            val canvas = pagina.canvas

            val pintura = Paint().apply {
                textSize = 20f
                isAntiAlias = true
            }

            canvas.drawText(
                "Informe mensual - Bitacora",
                50f,
                70f,
                pintura
            )

            pintura.textSize = 14f

            canvas.drawText(
                "Documento de prueba",
                50f,
                110f,
                pintura
            )

            canvas.drawText(
                "Generado desde la aplicacion Android",
                50f,
                140f,
                pintura
            )

            documento.finishPage(pagina)

            val carpeta = File(
                context.filesDir,
                "informes"
            )

            if (!carpeta.exists() && !carpeta.mkdirs()) {
                throw IllegalStateException(
                    "No se pudo crear la carpeta de informes"
                )
            }

            val archivo = File(
                carpeta,
                "informe_${UUID.randomUUID()}.pdf"
            )

            archivo.outputStream().use { salida ->
                documento.writeTo(salida)
            }

            return archivo

        } finally {
            documento.close()
        }
    }


    fun generarInformeMensual(
        bitacora: Bitacora,
        entradas: List<EntradaBitacora>
    ): File {

        val documento = PdfDocument()

        val anchoPagina = 595
        val altoPagina = 842

        val margen = 40f
        val limiteInferior = altoPagina - margen

        val pintura = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f
            color = android.graphics.Color.BLACK
        }

        var numeroPagina = 0
        var pagina: PdfDocument.Page? = null
        var posicionY = margen

        fun nuevaPagina() {
            pagina?.let {
                documento.finishPage(it)
            }

            numeroPagina++

            val info = PdfDocument.PageInfo.Builder(
                anchoPagina,
                altoPagina,
                numeroPagina
            ).create()

            pagina = documento.startPage(info)
            posicionY = margen
        }

        fun escribirTexto(
            texto: String,
            tamano: Float = 12f
        ) {
            pintura.textSize = tamano

            val canvas = pagina?.canvas ?: return

            val anchoDisponible = anchoPagina - margen * 2

            // Ajustar texto largo a varias líneas
            val palabras = texto.split(Regex("\\s+"))
            var linea = ""

            fun dibujarLinea(contenido: String) {
                if (posicionY + 20f > limiteInferior) {
                    nuevaPagina()
                }

                pagina?.canvas?.drawText(
                    contenido,
                    margen,
                    posicionY,
                    pintura
                )

                posicionY += tamano + 7f
            }

            for (palabra in palabras) {

                val propuesta = if (linea.isEmpty()) {
                    palabra
                } else {
                    "$linea $palabra"
                }

                if (
                    pintura.measureText(propuesta) > anchoDisponible &&
                    linea.isNotEmpty()
                ) {
                    dibujarLinea(linea)
                    linea = palabra
                } else {
                    linea = propuesta
                }
            }

            if (linea.isNotEmpty()) {
                dibujarLinea(linea)
            }
        }

        try {

            nuevaPagina()

            escribirTexto(
                "INFORME MENSUAL - BITÁCORA",
                18f
            )

            posicionY += 15f

            escribirTexto("Asignatura: ${bitacora.asignatura}")
            escribirTexto("Curso: ${bitacora.curso}")
            escribirTexto("Período: ${bitacora.mes} ${bitacora.anio}")
            escribirTexto("Cantidad de entradas: ${entradas.size}")

            posicionY += 20f

            entradas.forEachIndexed { indice, entrada ->

                if (posicionY + 100f > limiteInferior) {
                    nuevaPagina()
                }

                escribirTexto(
                    "ENTRADA ${indice + 1}",
                    15f
                )

                escribirTexto("Fecha: ${entrada.fecha}")
                escribirTexto("Estado: ${entrada.estado.name}")

                posicionY += 8f

                escribirTexto("Actividad:")
                escribirTexto(entrada.actividad)

                posicionY += 8f

                escribirTexto("Aprendizaje:")
                escribirTexto(entrada.aprendizaje)

                if (entrada.dudas.isNotBlank()) {
                    posicionY += 8f
                    escribirTexto("Dudas:")
                    escribirTexto(entrada.dudas)
                }

                if (entrada.comentarioProfesor.isNotBlank()) {
                    posicionY += 8f
                    escribirTexto("Comentario del profesor:")
                    escribirTexto(entrada.comentarioProfesor)
                }

                posicionY += 25f
            }

            pagina?.let {
                documento.finishPage(it)
            }

            val carpeta = File(
                context.filesDir,
                "informes"
            )

            if (!carpeta.exists() && !carpeta.mkdirs()) {
                throw IllegalStateException(
                    "No se pudo crear la carpeta de informes"
                )
            }

            val archivo = File(
                carpeta,
                "informe_mensual_${UUID.randomUUID()}.pdf"
            )

            archivo.outputStream().use { salida ->
                documento.writeTo(salida)
            }

            return archivo

        } finally {
            documento.close()
        }
    }

}
