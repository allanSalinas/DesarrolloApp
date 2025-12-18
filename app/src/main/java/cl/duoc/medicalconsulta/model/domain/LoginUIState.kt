package cl.duoc.medicalconsulta.model.domain

/**
 * Estado de la UI para la pantalla de inicio de sesión.
 *
 * @property username Nombre de usuario ingresado
 * @property password Contraseña ingresada
 * @property error Mensaje de error a mostrar (null si no hay error)
 * @property cargando Indica si se está procesando el login
 */
data class LoginUIState(
    val username: String = "",
    val password: String = "",
    val error: String? = null,
    val cargando: Boolean = false
)