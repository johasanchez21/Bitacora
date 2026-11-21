package cl.duoc.Bitacora.data.remote

import cl.duoc.Bitacora.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface BitacoraApiService {

    // Endpoints UsuarioController
    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): Response<List<UsuarioDto>>

    @POST("api/usuarios/login")
    suspend fun iniciarSesion(@Body credenciales: LoginRequestDto): Response<UsuarioDto>

    @POST("api/usuarios")
    suspend fun registrarUsuario(@Body usuario: UsuarioDto): Response<UsuarioDto>

    // Endpoints CursoController
    @GET("api/cursos")
    suspend fun obtenerCursos(): Response<List<CursoDto>>

    @GET("api/cursos/anio/{anio}")
    suspend fun obtenerCursosPorAnio(@Path("anio") anio: Int): Response<List<CursoDto>>

    @POST("api/cursos")
    suspend fun registrarCurso(@Body curso: CursoDto): Response<CursoDto>

    // Endpoints BitacoraController
    @GET("api/bitacoras")
    suspend fun obtenerTodasLasBitacoras(): Response<List<BitacoraDto>>

    @GET("api/bitacoras/curso/{cursoId}")
    suspend fun obtenerBitacorasPorCurso(@Path("cursoId") cursoId: Long): Response<List<BitacoraDto>>

    @POST("api/bitacoras")
    suspend fun crearBitacora(@Body bitacora: BitacoraDto): Response<BitacoraDto>

    @PUT("api/bitacoras/{id}/publicar")
    suspend fun publicarBitacora(@Path("id") id: Long): Response<BitacoraDto>

    // Endpoints EntradaBitacoraController
    @GET("api/entradas")
    suspend fun obtenerTodasLasEntradas(): Response<List<EntradaBitacoraDto>>

    @GET("api/entradas/estudiante/{estudianteId}")
    suspend fun obtenerEntradasPorEstudiante(@Path("estudianteId") estudianteId: Long): Response<List<EntradaBitacoraDto>>

    @GET("api/entradas/enviadas")
    suspend fun obtenerEntradasEnviadas(): Response<List<EntradaBitacoraDto>>

    @POST("api/entradas")
    suspend fun crearEntrada(@Body entrada: EntradaBitacoraDto): Response<EntradaBitacoraDto>

    @PUT("api/entradas/{id}/revisar")
    suspend fun revisarEntrada(@Path("id") id: Long, @Body request: RevisarEntradaRequestDto): Response<EntradaBitacoraDto>
}
