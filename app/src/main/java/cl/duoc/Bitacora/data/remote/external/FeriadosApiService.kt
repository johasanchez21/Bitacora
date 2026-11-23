package cl.duoc.Bitacora.data.remote.external

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class FeriadoDto(
    @SerializedName("date") val date: String,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: String,
    @SerializedName("inalienable") val inalienable: Boolean
)

data class FeriadosResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<FeriadoDto>
)

interface FeriadosApiService {
    @GET("holidays.json")
    suspend fun obtenerFeriados(): Response<FeriadosResponseDto>
}

object ExternalApiClient {
    private const val BASE_URL = "https://api.boostr.cl/"

    val feriadosService: FeriadosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeriadosApiService::class.java)
    }
}
