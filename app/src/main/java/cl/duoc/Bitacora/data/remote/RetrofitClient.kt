package cl.duoc.Bitacora.data.remote

import android.os.Build
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private fun esEmulador(): Boolean {
        return try {
            val brand = Build.BRAND ?: ""
            val device = Build.DEVICE ?: ""
            val fingerprint = Build.FINGERPRINT ?: ""
            val hardware = Build.HARDWARE ?: ""
            val model = Build.MODEL ?: ""
            val manufacturer = Build.MANUFACTURER ?: ""
            val product = Build.PRODUCT ?: ""

            (brand.startsWith("generic") && device.startsWith("generic"))
                    || fingerprint.startsWith("generic")
                    || fingerprint.startsWith("unknown")
                    || hardware.contains("goldfish")
                    || hardware.contains("ranchu")
                    || model.contains("google_sdk")
                    || model.contains("Emulator")
                    || model.contains("Android SDK built for x86")
                    || manufacturer.contains("Genymotion")
                    || product.contains("sdk_gphone")
                    || product.contains("vbox86p")
                    || product.contains("emulator")
                    || product.contains("simulator")
        } catch (e: Throwable) {
            false
        }
    }

    private val BASE_URL: String = if (esEmulador()) {
        "http://10.0.2.2:8080/"
    } else {
        "http://localhost:8080/"
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: BitacoraApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BitacoraApiService::class.java)
    }
}
