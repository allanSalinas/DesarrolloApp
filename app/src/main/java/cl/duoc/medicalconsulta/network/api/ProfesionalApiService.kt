package cl.duoc.medicalconsulta.network.api

import cl.duoc.medicalconsulta.network.dto.ProfesionalDto
import retrofit2.Response
import retrofit2.http.*

interface ProfesionalApiService {

    @GET("profesionales")
    suspend fun obtenerTodos(): Response<List<ProfesionalDto>>

    @GET("profesionales/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<ProfesionalDto>

    @GET("profesionales/disponibles")
    suspend fun obtenerDisponibles(): Response<List<ProfesionalDto>>

    @GET("profesionales/especialidad/{especialidad}")
    suspend fun obtenerPorEspecialidad(@Path("especialidad") especialidad: String): Response<List<ProfesionalDto>>

    @POST("profesionales")
    suspend fun crear(@Body profesional: ProfesionalDto): Response<ProfesionalDto>

    @PUT("profesionales/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body profesional: ProfesionalDto): Response<ProfesionalDto>

    @PATCH("profesionales/{id}/disponibilidad")
    suspend fun cambiarDisponibilidad(
        @Path("id") id: Long,
        @Query("disponible") disponible: Boolean
    ): Response<ProfesionalDto>

    @DELETE("profesionales/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}
