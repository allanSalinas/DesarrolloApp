package cl.duoc.medicalconsulta.network

import cl.duoc.medicalconsulta.network.api.MedicamentoApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente de Retrofit para OpenFDA API
 */
object OpenFdaClient {

    // URL base de OpenFDA
    private const val BASE_URL = "https://api.fda.gov/"

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

    // Instancia de Retrofit para OpenFDA
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Service para Medicamentos (OpenFDA)
    val medicamentoApiService: MedicamentoApiService by lazy {
        retrofit.create(MedicamentoApiService::class.java)
    }
}
