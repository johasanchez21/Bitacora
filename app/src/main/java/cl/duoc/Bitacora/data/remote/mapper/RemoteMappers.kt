package cl.duoc.Bitacora.data.remote.mapper

import cl.duoc.Bitacora.data.remote.dto.*
import cl.duoc.Bitacora.domain.model.*

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        id = (id ?: 0L).toInt(),
        nombre = nombre,
        correo = correo,
        password = password,
        rol = try { RolUsuario.valueOf(rol) } catch (e: Exception) { RolUsuario.Estudiante },
        cursoIds = cursoIds?.map { it.toInt() } ?: emptyList()
    )
}

fun Usuario.toDto(): UsuarioDto {
    return UsuarioDto(
        id = if (id > 0) id.toLong() else null,
        nombre = nombre,
        correo = correo,
        password = password,
        rol = rol.name,
        cursoIds = cursoIds.map { it.toLong() }
    )
}

fun CursoDto.toDomain(): Curso {
    return Curso(
        id = (id ?: 0L).toInt(),
        nombre = nombre,
        codigo = codigo,
        anio = anio,
        semestre = semestre
    )
}

fun Curso.toDto(): CursoDto {
    return CursoDto(
        id = if (id > 0) id.toLong() else null,
        nombre = nombre,
        codigo = codigo,
        anio = anio,
        semestre = semestre
    )
}

fun BitacoraDto.toDomain(): Bitacora {
    return Bitacora(
        id = (id ?: 0L).toInt(),
        asignatura = asignatura,
        mes = mes,
        anio = anio,
        cursoId = (cursoId ?: 0L).toInt(),
        curso = curso,
        profesorId = (profesorId ?: 0L).toInt(),
        estado = try { EstadoBitacora.valueOf(estado) } catch (e: Exception) { EstadoBitacora.BORRADOR }
    )
}

fun Bitacora.toDto(): BitacoraDto {
    return BitacoraDto(
        id = if (id > 0) id.toLong() else null,
        asignatura = asignatura,
        mes = mes,
        anio = anio,
        cursoId = if (cursoId > 0) cursoId.toLong() else null,
        curso = curso,
        profesorId = if (profesorId > 0) profesorId.toLong() else null,
        estado = estado.name
    )
}

fun EntradaBitacoraDto.toDomain(): EntradaBitacora {
    return EntradaBitacora(
        id = (id ?: 0L).toInt(),
        bitacoraId = bitacoraId.toInt(),
        estudianteId = estudianteId.toInt(),
        fecha = fecha,
        actividad = actividad,
        aprendizaje = aprendizaje,
        dudas = dudas ?: "",
        estado = try { EstadoEntrada.valueOf(estado) } catch (e: Exception) { EstadoEntrada.BORRADOR },
        comentarioProfesor = comentarioProfesor ?: "",
        fotoUri = fotoUri
    )
}

fun EntradaBitacora.toDto(): EntradaBitacoraDto {
    return EntradaBitacoraDto(
        id = if (id > 0) id.toLong() else null,
        bitacoraId = bitacoraId.toLong(),
        estudianteId = estudianteId.toLong(),
        fecha = fecha,
        actividad = actividad,
        aprendizaje = aprendizaje,
        dudas = dudas,
        estado = estado.name,
        comentarioProfesor = comentarioProfesor,
        fotoUri = fotoUri
    )
}
