package cl.duoc.medicalconsulta.network

import cl.duoc.medicalconsulta.network.api.CitaApiService
import cl.duoc.medicalconsulta.network.api.ProfesionalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URL base del backend
    // IMPORTANTE: Para emulador Android usar 10.0.2.2 en lugar de localhost
    // Para dispositivo físico, usar la IP local de tu computadora (ej: 192.168.1.X)
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Logging interceptor para debug
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp con configuración personalizada
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Service para Profesionales
    val profesionalApiService: ProfesionalApiService by lazy {
        retrofit.create(ProfesionalApiService::class.java)
    }

    // API Service para Citas
    val citaApiService: CitaApiService by lazy {
        retrofit.create(CitaApiService::class.java)
    }
}
