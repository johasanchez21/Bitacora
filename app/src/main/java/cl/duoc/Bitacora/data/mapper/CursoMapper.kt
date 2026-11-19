package cl.duoc.Bitacora.data.mapper

import cl.duoc.Bitacora.data.local.entity.CursoEntity
import cl.duoc.Bitacora.domain.model.Curso

fun Curso.toEntity(): CursoEntity {
    return CursoEntity(
        id = id,
        nombre = nombre,
        codigo = codigo,
        anio = anio,
        semestre = semestre
    )
}

fun CursoEntity.toModel(): Curso {
    return Curso(
        id = id,
        nombre = nombre,
        codigo = codigo,
        anio = anio,
        semestre = semestre
    )
}
