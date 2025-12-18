package cl.duoc.medicalconsulta.model.domain

/**
 * Clase de dominio que representa un usuario en el sistema.
 * Esta es la representación de negocio, independiente de la capa de datos o red.
 *
 * @property id Identificador único del usuario
 * @property username Nombre de usuario para autenticación
 * @property nombreCompleto Nombre completo del usuario
 * @property email Correo electrónico del usuario
 * @property rut RUT chileno del usuario (formato: 12345678-9)
 * @property rol Rol del usuario en el sistema
 * @property fotoPerfil URL o path de la foto de perfil (opcional)
 * @property telefono Número de teléfono de contacto (opcional)
 * @property activo Indica si el usuario está activo en el sistema
 */
data class Usuario(
    val id: Long = 0L,
    val username: String,
    val nombreCompleto: String,
    val email: String,
    val rut: String,
    val rol: Rol,
    val fotoPerfil: String? = null,
    val telefono: String? = null,
    val activo: Boolean = true
) {
    /**
     * Obtiene las iniciales del usuario basándose en su nombre completo
     * Útil para mostrar en avatares cuando no hay foto de perfil
     */
    fun getIniciales(): String {
        val partes = nombreCompleto.trim().split(" ")
        return when {
            partes.size >= 2 -> "${partes[0].firstOrNull()?.uppercase() ?: ""}${partes[1].firstOrNull()?.uppercase() ?: ""}"
            partes.size == 1 && partes[0].isNotEmpty() -> partes[0].take(2).uppercase()
            else -> "US"
        }
    }

    /**
     * Verifica si el usuario tiene una foto de perfil válida
     */
    fun tieneFotoPerfil(): Boolean = !fotoPerfil.isNullOrBlank()

    /**
     * Valida si el RUT tiene un formato básicamente correcto
     */
    fun rutEsValido(): Boolean {
        val rutPattern = "^\\d{7,8}-[0-9Kk]$".toRegex()
        return rut.matches(rutPattern)
    }
}
