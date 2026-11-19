package cl.duoc.Bitacora.data.mapper

import cl.duoc.Bitacora.data.local.entity.BitacoraEntity
import cl.duoc.Bitacora.domain.model.Bitacora
import cl.duoc.Bitacora.domain.model.EstadoBitacora

fun Bitacora.toEntity(): BitacoraEntity {
    return BitacoraEntity(
        id = id,
        asignatura = asignatura,
        mes = mes,
        anio = anio,
        cursoId = cursoId,
        curso = curso,
        profesorId = profesorId,
        estado = estado.name
    )
}

fun BitacoraEntity.toModel(): Bitacora {
    return Bitacora(
        id = id,
        asignatura = asignatura,
        mes = mes,
        anio = anio,
        cursoId = cursoId,
        curso = curso,
        profesorId = profesorId,
        estado = try {
            EstadoBitacora.valueOf(estado)
        } catch (e: Exception) {
            EstadoBitacora.BORRADOR
        }
    )
}
