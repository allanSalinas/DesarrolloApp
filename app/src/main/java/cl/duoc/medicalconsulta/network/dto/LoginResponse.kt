package cl.duoc.medicalconsulta.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta del inicio de sesión.
 * Recibido desde el endpoint de autenticación del backend.
 *
 * @property success Indica si la autenticación fue exitosa
 * @property mensaje Mensaje descriptivo del resultado (error o éxito)
 * @property usuario Datos del usuario autenticado (solo si success = true)
 * @property token Token de autenticación JWT (opcional)
 */
data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("usuario")
    val usuario: UsuarioDto? = null,

    @SerializedName("token")
    val token: String? = null
)
