package cl.duoc.medicalconsulta.network.api

import cl.duoc.medicalconsulta.network.dto.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface Retrofit para comunicación con el API de usuarios.
 * Define todos los endpoints relacionados con gestión de usuarios y autenticación.
 */
interface UsuarioApiService {

    /**
     * Endpoint de inicio de sesión.
     * POST /api/usuarios/login
     *
     * @param request Datos de autenticación (username y password)
     * @return Response con LoginResponse conteniendo usuario y token
     */
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Endpoint de registro de nuevo usuario.
     * POST /api/usuarios/registro
     *
     * @param usuario Datos del nuevo usuario
     * @return Response con el usuario creado
     */
    @POST("api/usuarios/registro")
    suspend fun registro(@Body usuario: UsuarioDto): Response<UsuarioDto>

    /**
     * Endpoint para solicitar recuperación de contraseña.
     * Envía un email con un token de recuperación.
     * POST /api/usuarios/recuperar-password
     *
     * @param request Email del usuario
     * @return Response con mensaje de confirmación
     */
    @POST("api/usuarios/recuperar-password")
    suspend fun recuperarPassword(@Body request: RecuperacionPasswordRequest): Response<RecuperacionResponse>

    /**
     * Endpoint para restablecer contraseña usando el token recibido.
     * POST /api/usuarios/restablecer-password
     *
     * @param request Token y nueva contraseña
     * @return Response con mensaje de confirmación
     */
    @POST("api/usuarios/restablecer-password")
    suspend fun restablecerPassword(@Body request: RestablecerPasswordRequest): Response<RecuperacionResponse>

    /**
     * Obtiene los datos de un usuario específico por ID.
     * GET /api/usuarios/{id}
     *
     * @param id ID del usuario
     * @return Response con los datos del usuario
     */
    @GET("api/usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long): Response<UsuarioDto>

    /**
     * Actualiza los datos de un usuario.
     * PUT /api/usuarios/{id}
     *
     * @param id ID del usuario a actualizar
     * @param usuario Datos actualizados
     * @return Response con el usuario actualizado
     */
    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: UsuarioDto
    ): Response<UsuarioDto>

    /**
     * Actualiza parcialmente un usuario (solo campos enviados).
     * PATCH /api/usuarios/{id}
     *
     * @param id ID del usuario
     * @param campos Mapa con los campos a actualizar
     * @return Response con el usuario actualizado
     */
    @PATCH("api/usuarios/{id}")
    suspend fun actualizarParcial(
        @Path("id") id: Long,
        @Body campos: Map<String, Any>
    ): Response<UsuarioDto>

    /**
     * Actualiza la foto de perfil de un usuario.
     * PATCH /api/usuarios/{id}/foto
     *
     * @param id ID del usuario
     * @param foto Archivo de imagen en formato multipart
     * @return Response con el usuario actualizado (incluye nueva URL de foto)
     */
    @Multipart
    @PATCH("api/usuarios/{id}/foto")
    suspend fun actualizarFotoPerfil(
        @Path("id") id: Long,
        @Part foto: MultipartBody.Part
    ): Response<UsuarioDto>

    /**
     * Elimina (desactiva) un usuario.
     * DELETE /api/usuarios/{id}
     *
     * @param id ID del usuario a eliminar
     * @return Response vacío con código de estado
     */
    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<Void>

    /**
     * Obtiene todos los usuarios (requiere permisos de administrador).
     * GET /api/usuarios
     *
     * @return Response con lista de usuarios
     */
    @GET("api/usuarios")
    suspend fun getAllUsuarios(): Response<List<UsuarioDto>>

    /**
     * Obtiene usuarios por rol.
     * GET /api/usuarios/rol/{rol}
     *
     * @param rol Rol a filtrar (PACIENTE, MEDICO, etc.)
     * @return Response con lista de usuarios con ese rol
     */
    @GET("api/usuarios/rol/{rol}")
    suspend fun getUsuariosByRol(@Path("rol") rol: String): Response<List<UsuarioDto>>

    /**
     * Verifica si un username está disponible.
     * GET /api/usuarios/verificar/username/{username}
     *
     * @param username Username a verificar
     * @return Response con disponibilidad (true/false)
     */
    @GET("api/usuarios/verificar/username/{username}")
    suspend fun verificarUsername(@Path("username") username: String): Response<Map<String, Boolean>>

    /**
     * Verifica si un email está disponible.
     * GET /api/usuarios/verificar/email/{email}
     *
     * @param email Email a verificar
     * @return Response con disponibilidad (true/false)
     */
    @GET("api/usuarios/verificar/email/{email}")
    suspend fun verificarEmail(@Path("email") email: String): Response<Map<String, Boolean>>
}
