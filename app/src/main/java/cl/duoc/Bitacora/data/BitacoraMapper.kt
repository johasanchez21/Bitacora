package cl.duoc.Bitacora.data

import cl.duoc.Bitacora.data.local.BitacoraEntity
import cl.duoc.Bitacora.model.Bitacora
import cl.duoc.Bitacora.model.EstadoBitacora

fun Bitacora.toEntity(): BitacoraEntity {
    return BitacoraEntity(
        id = id,
        asignatura = asignatura,
        mes = mes,
        anio = anio,
        curso = curso,
        estado = estado.name
    )
}

fun BitacoraEntity.toModel(): Bitacora {
    return Bitacora(
        id = id,
        asignatura = asignatura,
        mes = mes,
        anio = anio,
        curso = curso,
        estado = EstadoBitacora.valueOf(estado)
    )
}
