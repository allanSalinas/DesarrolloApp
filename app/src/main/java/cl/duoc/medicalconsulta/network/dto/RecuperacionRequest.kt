package cl.duoc.medicalconsulta.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitar recuperación de contraseña.
 * Envía el email del usuario para recibir un token de recuperación.
 *
 * @property email Correo electrónico del usuario
 */
data class RecuperacionPasswordRequest(
    @SerializedName("email")
    val email: String
)

/**
 * DTO para restablecer la contraseña con el token recibido.
 *
 * @property token Token de recuperación enviado al email
 * @property nuevaPassword Nueva contraseña del usuario
 */
data class RestablecerPasswordRequest(
    @SerializedName("token")
    val token: String,

    @SerializedName("nuevaPassword")
    val nuevaPassword: String
)

/**
 * DTO para la respuesta de operaciones de recuperación/restablecimiento.
 *
 * @property success Indica si la operación fue exitosa
 * @property mensaje Mensaje descriptivo del resultado
 */
data class RecuperacionResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("mensaje")
    val mensaje: String
)
