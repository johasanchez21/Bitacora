package cl.duoc.Bitacora.data

import cl.duoc.Bitacora.model.Bitacora
import cl.duoc.Bitacora.model.EstadoBitacora


class BitacoraRepository {

    private val bitacoras = listOf(
        Bitacora(
            id = 1,
            asignatura = "Matemática",
            mes = "Octubre",
            anio = 2026,
            curso = "7° Básico A",
            estado = EstadoBitacora.PUBLICADA
        ),
        Bitacora(
            id = 2,
            asignatura = "Lenguaje",
            mes = "Octubre",
            anio = 2026,
            curso = "7° Básico A",
            estado = EstadoBitacora.PUBLICADA
        ),
        Bitacora(
            id = 3,
            asignatura = "Ciencias Naturales",
            mes = "Octubre",
            anio = 2026,
            curso = "7° Básico A",
            estado = EstadoBitacora.PUBLICADA
        )
    )

    fun obtenerBitacorasPublicadas(curso: String): List<Bitacora> {
        return bitacoras.filter {
            it.curso == curso &&
                    it.estado == EstadoBitacora.PUBLICADA
        }
    }
}

