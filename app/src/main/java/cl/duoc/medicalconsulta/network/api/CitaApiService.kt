package cl.duoc.medicalconsulta.network.api

import cl.duoc.medicalconsulta.network.dto.CitaDto
import retrofit2.Response
import retrofit2.http.*

interface CitaApiService {

    @GET("citas")
    suspend fun obtenerTodas(): Response<List<CitaDto>>

    @GET("citas/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<CitaDto>

    
    @GET("citas/paciente/{rut}")
    suspend fun obtenerPorRutPaciente(@Path("rut") rut: String): Response<List<CitaDto>>

    @GET("citas/profesional/{id}")
    suspend fun obtenerPorProfesional(@Path("id") id: Long): Response<List<CitaDto>>

    @GET("citas/fecha/{fecha}")
    suspend fun obtenerPorFecha(@Path("fecha") fecha: String): Response<List<CitaDto>>

    @POST("citas")
    suspend fun crear(@Body cita: CitaDto): Response<CitaDto>

    @PUT("citas/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body cita: CitaDto): Response<CitaDto>

    @DELETE("citas/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}
