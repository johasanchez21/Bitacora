package cl.duoc.Bitacora.data.mapper

import cl.duoc.Bitacora.data.local.entity.EntradaBitacoraEntity
import cl.duoc.Bitacora.domain.model.EntradaBitacora
import cl.duoc.Bitacora.domain.model.EstadoEntrada

fun EntradaBitacora.toEntity(): EntradaBitacoraEntity {
    return EntradaBitacoraEntity(
        id = id,
        bitacoraId = bitacoraId,
        estudianteId = estudianteId,
        fecha = fecha,
        actividad = actividad,
        aprendizaje = aprendizaje,
        dudas = dudas,
        estado = estado.name,
        comentarioProfesor = comentarioProfesor,
        fotoUri = fotoUri
    )
}

fun EntradaBitacoraEntity.toModel(): EntradaBitacora {
    return EntradaBitacora(
        id = id,
        bitacoraId = bitacoraId,
        estudianteId = estudianteId,
        fecha = fecha,
        actividad = actividad,
        aprendizaje = aprendizaje,
        dudas = dudas,
        estado = EstadoEntrada.valueOf(estado),
        comentarioProfesor = comentarioProfesor,
        fotoUri = fotoUri
    )
}
