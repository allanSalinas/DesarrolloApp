package cl.duoc.medicalconsulta.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de inicio de sesión.
 * Se envía al endpoint de autenticación del backend.
 *
 * @property username Nombre de usuario o email
 * @property password Contraseña del usuario
 */
data class LoginRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
)
