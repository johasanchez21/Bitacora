package cl.duoc.Bitacora.data

import cl.duoc.Bitacora.data.local.EntradaBitacoraEntity
import cl.duoc.Bitacora.model.EntradaBitacora
import cl.duoc.Bitacora.model.EstadoEntrada

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

