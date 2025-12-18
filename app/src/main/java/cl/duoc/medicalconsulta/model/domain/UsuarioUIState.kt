package cl.duoc.medicalconsulta.model.domain

/**
 * Estado de la UI para formularios de usuario (registro y edición de perfil).
 * Incluye todos los campos del formulario y sus errores de validación.
 */
data class UsuarioUIState(
    // Datos del formulario
    val username: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val nombreCompleto: String = "",
    val email: String = "",
    val rut: String = "",
    val telefono: String = "",
    val rol: Rol = Rol.PACIENTE,
    val fotoPerfil: String? = null,

    // Errores de validación por campo
    val errores: UsuarioErrores = UsuarioErrores(),

    // Estados de la operación
    val cargando: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null,

    // Modo de edición (true = editar perfil, false = registro)
    val modoEdicion: Boolean = false,
    val usuarioId: Long? = null
) {
    /**
     * Verifica si el formulario es válido (no tiene errores)
     */
    fun esValido(): Boolean = errores.esValido()

    /**
     * Verifica si todos los campos obligatorios están completos
     */
    fun camposCompletos(): Boolean {
        return username.isNotBlank() &&
                nombreCompleto.isNotBlank() &&
                email.isNotBlank() &&
                rut.isNotBlank() &&
                // En modo edición, password es opcional
                (modoEdicion || password.isNotBlank())
    }

    /**
     * Convierte el estado del formulario a un objeto Usuario de dominio
     */
    fun toUsuario(): Usuario {
        return Usuario(
            id = usuarioId ?: 0L,
            username = username.trim(),
            nombreCompleto = nombreCompleto.trim(),
            email = email.trim().lowercase(),
            rut = rut.trim(),
            rol = rol,
            fotoPerfil = fotoPerfil,
            telefono = telefono.trim().ifBlank { null },
            activo = true
        )
    }

    companion object {
        /**
         * Crea un estado inicial desde un Usuario existente (para edición)
         */
        fun fromUsuario(usuario: Usuario): UsuarioUIState {
            return UsuarioUIState(
                username = usuario.username,
                nombreCompleto = usuario.nombreCompleto,
                email = usuario.email,
                rut = usuario.rut,
                telefono = usuario.telefono ?: "",
                rol = usuario.rol,
                fotoPerfil = usuario.fotoPerfil,
                modoEdicion = true,
                usuarioId = usuario.id
            )
        }
    }
}

/**
 * Data class que encapsula todos los errores de validación del formulario de usuario.
 */
data class UsuarioErrores(
    val username: String? = null,
    val password: String? = null,
    val confirmarPassword: String? = null,
    val nombreCompleto: String? = null,
    val email: String? = null,
    val rut: String? = null,
    val telefono: String? = null,
    val general: String? = null
) {
    /**
     * Verifica si no hay ningún error
     */
    fun esValido(): Boolean {
        return username == null &&
                password == null &&
                confirmarPassword == null &&
                nombreCompleto == null &&
                email == null &&
                rut == null &&
                telefono == null &&
                general == null
    }

    /**
     * Verifica si hay al menos un error
     */
    fun tieneErrores(): Boolean = !esValido()
}

/**
 * Estado de la UI para el proceso de recuperación de contraseña.
 */
data class RecuperacionPasswordUIState(
    // Paso 1: Solicitar recuperación
    val email: String = "",
    val emailError: String? = null,

    // Paso 2: Restablecer con token
    val token: String = "",
    val tokenError: String? = null,
    val nuevaPassword: String = "",
    val nuevaPasswordError: String? = null,
    val confirmarPassword: String = "",
    val confirmarPasswordError: String? = null,

    // Estados
    val paso: Int = 1, // 1 = solicitar, 2 = restablecer
    val cargando: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
) {
    /**
     * Valida el paso 1 (solicitar recuperación)
     */
    fun paso1Valido(): Boolean = email.isNotBlank() && emailError == null

    /**
     * Valida el paso 2 (restablecer contraseña)
     */
    fun paso2Valido(): Boolean {
        return token.isNotBlank() && tokenError == null &&
                nuevaPassword.isNotBlank() && nuevaPasswordError == null &&
                confirmarPassword.isNotBlank() && confirmarPasswordError == null
    }
}

/**
 * Estado de la UI para la pantalla de perfil de usuario.
 */
data class PerfilUIState(
    val usuario: Usuario? = null,
    val cargando: Boolean = false,
    val modoEdicion: Boolean = false,
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
    val mostrarDialogoFoto: Boolean = false,
    val mostrarDialogoCerrarSesion: Boolean = false
)
