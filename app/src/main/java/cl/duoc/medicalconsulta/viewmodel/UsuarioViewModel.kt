package cl.duoc.medicalconsulta.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.medicalconsulta.model.data.repository.UsuarioRepository
import cl.duoc.medicalconsulta.model.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de usuarios (registro, actualización de perfil).
 * Maneja la lógica de negocio y validación de formularios.
 *
 * @property context Contexto de la aplicación
 */
class UsuarioViewModel(private val context: Context) : ViewModel() {

    private val repository = UsuarioRepository(context)

    // Estado del formulario de usuario
    private val _estado = MutableStateFlow(UsuarioUIState())
    val estado: StateFlow<UsuarioUIState> = _estado.asStateFlow()

    // Estado de recuperación de contraseña
    private val _estadoRecuperacion = MutableStateFlow(RecuperacionPasswordUIState())
    val estadoRecuperacion: StateFlow<RecuperacionPasswordUIState> = _estadoRecuperacion.asStateFlow()

    // Estado del perfil
    private val _estadoPerfil = MutableStateFlow(PerfilUIState())
    val estadoPerfil: StateFlow<PerfilUIState> = _estadoPerfil.asStateFlow()

    // ==================== FORMULARIO DE REGISTRO/EDICIÓN ====================

    /**
     * Carga los datos de un usuario para edición.
     */
    fun cargarUsuarioParaEdicion(usuarioId: Long) {
        viewModelScope.launch {
            val result = repository.getUsuarioById(usuarioId)
            result.onSuccess { usuario ->
                _estado.update { UsuarioUIState.fromUsuario(usuario) }
            }.onFailure { error ->
                _estado.update { it.copy(mensajeError = error.message) }
            }
        }
    }

    fun onUsernameChange(value: String) {
        _estado.update { it.copy(username = value) }
        validarUsername(value)
    }

    fun onPasswordChange(value: String) {
        _estado.update { it.copy(password = value) }
        validarPassword(value)
    }

    fun onConfirmarPasswordChange(value: String) {
        _estado.update { it.copy(confirmarPassword = value) }
        validarConfirmarPassword(value)
    }

    fun onNombreCompletoChange(value: String) {
        _estado.update { it.copy(nombreCompleto = value) }
        validarNombreCompleto(value)
    }

    fun onEmailChange(value: String) {
        _estado.update { it.copy(email = value) }
        validarEmail(value)
    }

    fun onRutChange(value: String) {
        _estado.update { it.copy(rut = value) }
        validarRut(value)
    }

    fun onTelefonoChange(value: String) {
        _estado.update { it.copy(telefono = value) }
        validarTelefono(value)
    }

    fun onRolChange(value: Rol) {
        _estado.update { it.copy(rol = value) }
    }

    fun onFotoPerfilChange(value: String?) {
        _estado.update { it.copy(fotoPerfil = value) }
    }

    // ==================== VALIDACIONES ====================

    private fun validarUsername(username: String) {
        val error = when {
            username.isBlank() -> "El nombre de usuario es obligatorio"
            username.length < 4 -> "Debe tener al menos 4 caracteres"
            username.length > 20 -> "Máximo 20 caracteres"
            !username.matches("^[a-zA-Z0-9._]+$".toRegex()) ->
                "Solo se permiten letras, números, punto y guión bajo"
            else -> null
        }
        _estado.update { it.copy(errores = it.errores.copy(username = error)) }
    }

    private fun validarPassword(password: String) {
        // Si está en modo edición y el campo está vacío, no hay error
        if (_estado.value.modoEdicion && password.isBlank()) {
            _estado.update { it.copy(errores = it.errores.copy(password = null)) }
            return
        }

        val error = when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "Debe tener al menos 6 caracteres"
            password.length > 50 -> "Máximo 50 caracteres"
            else -> null
        }
        _estado.update { it.copy(errores = it.errores.copy(password = error)) }

        // Re-valida confirmar contraseña si ya tiene valor
        if (_estado.value.confirmarPassword.isNotBlank()) {
            validarConfirmarPassword(_estado.value.confirmarPassword)
        }
    }

    private fun validarConfirmarPassword(confirmar: String) {
        val error = when {
            confirmar.isBlank() && _estado.value.password.isNotBlank() ->
                "Debes confirmar la contraseña"
            confirmar != _estado.value.password ->
                "Las contraseñas no coinciden"
            else -> null
        }
        _estado.update { it.copy(errores = it.errores.copy(confirmarPassword = error)) }
    }

    private fun validarNombreCompleto(nombre: String) {
        val error = when {
            nombre.isBlank() -> "El nombre completo es obligatorio"
            nombre.length < 3 -> "Debe tener al menos 3 caracteres"
            nombre.length > 100 -> "Máximo 100 caracteres"
            !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$".toRegex()) ->
                "Solo se permiten letras y espacios"
            else -> null
        }
        _estado.update { it.copy(errores = it.errores.copy(nombreCompleto = error)) }
    }

    private fun validarEmail(email: String) {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        val error = when {
            email.isBlank() -> "El email es obligatorio"
            !email.matches(emailPattern) -> "Email inválido"
            else -> null
        }
        _estado.update { it.copy(errores = it.errores.copy(email = error)) }
    }

    private fun validarRut(rut: String) {
        val rutPattern = "^\\d{7,8}-[0-9Kk]$".toRegex()
        val error = when {
            rut.isBlank() -> "El RUT es obligatorio"
            !rut.matches(rutPattern) -> "Formato inválido (ej: 12345678-9)"
            !validarDigitoVerificadorRut(rut) -> "RUT inválido"
            else -> null
        }
        _estado.update { it.copy(errores = it.errores.copy(rut = error)) }
    }

    private fun validarTelefono(telefono: String) {
        if (telefono.isBlank()) {
            _estado.update { it.copy(errores = it.errores.copy(telefono = null)) }
            return
        }

        val telefonoPattern = "^[+]?[0-9]{8,15}$".toRegex()
        val error = when {
            !telefono.matches(telefonoPattern) -> "Teléfono inválido (8-15 dígitos)"
            else -> null
        }
        _estado.update { it.copy(errores = it.errores.copy(telefono = error)) }
    }

    /**
     * Valida el dígito verificador de un RUT chileno.
     */
    private fun validarDigitoVerificadorRut(rut: String): Boolean {
        try {
            val partes = rut.split("-")
            if (partes.size != 2) return false

            val numero = partes[0].toIntOrNull() ?: return false
            val dv = partes[1].uppercase()

            var suma = 0
            var multiplicador = 2
            var numTemp = numero

            while (numTemp > 0) {
                suma += (numTemp % 10) * multiplicador
                numTemp /= 10
                multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
            }

            val resto = suma % 11
            val dvCalculado = when (11 - resto) {
                11 -> "0"
                10 -> "K"
                else -> (11 - resto).toString()
            }

            return dv == dvCalculado
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Valida todos los campos del formulario.
     */
    fun validarTodosCampos() {
        val s = _estado.value
        validarUsername(s.username)
        if (!s.modoEdicion || s.password.isNotBlank()) {
            validarPassword(s.password)
            validarConfirmarPassword(s.confirmarPassword)
        }
        validarNombreCompleto(s.nombreCompleto)
        validarEmail(s.email)
        validarRut(s.rut)
        if (s.telefono.isNotBlank()) {
            validarTelefono(s.telefono)
        }
    }

    // ==================== REGISTRO Y ACTUALIZACIÓN ====================

    /**
     * Registra un nuevo usuario.
     */
    fun registrar(onSuccess: (Usuario) -> Unit) {
        validarTodosCampos()

        val s = _estado.value
        if (!s.esValido() || !s.camposCompletos()) {
            _estado.update {
                it.copy(mensajeError = "Por favor, completa todos los campos correctamente")
            }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, mensajeError = null) }

            val usuario = s.toUsuario()
            val result = repository.registro(usuario, s.password)

            result.onSuccess { usuarioCreado ->
                _estado.update {
                    it.copy(
                        cargando = false,
                        mensajeExito = "Usuario registrado exitosamente"
                    )
                }
                onSuccess(usuarioCreado)
            }.onFailure { error ->
                _estado.update {
                    it.copy(
                        cargando = false,
                        mensajeError = error.message ?: "Error al registrar usuario"
                    )
                }
            }
        }
    }

    /**
     * Actualiza el perfil de un usuario existente.
     */
    fun actualizarPerfil(onSuccess: (Usuario) -> Unit) {
        validarTodosCampos()

        val s = _estado.value
        if (!s.esValido()) {
            _estado.update {
                it.copy(mensajeError = "Por favor, corrige los errores del formulario")
            }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, mensajeError = null) }

            val usuario = s.toUsuario()
            val result = repository.actualizarPerfil(usuario)

            result.onSuccess { usuarioActualizado ->
                _estado.update {
                    it.copy(
                        cargando = false,
                        mensajeExito = "Perfil actualizado exitosamente"
                    )
                }
                onSuccess(usuarioActualizado)
            }.onFailure { error ->
                _estado.update {
                    it.copy(
                        cargando = false,
                        mensajeError = error.message ?: "Error al actualizar perfil"
                    )
                }
            }
        }
    }

    /**
     * Actualiza la foto de perfil del usuario.
     */
    fun actualizarFoto(usuarioId: Long, fotoUri: Uri, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }

            val result = repository.actualizarFoto(usuarioId, fotoUri)

            result.onSuccess {
                _estado.update {
                    it.copy(
                        cargando = false,
                        fotoPerfil = fotoUri.toString(),
                        mensajeExito = "Foto actualizada"
                    )
                }
                onSuccess()
            }.onFailure { error ->
                _estado.update {
                    it.copy(
                        cargando = false,
                        mensajeError = error.message ?: "Error al actualizar foto"
                    )
                }
            }
        }
    }

    /**
     * Limpia los mensajes de estado.
     */
    fun limpiarMensajes() {
        _estado.update { it.copy(mensajeExito = null, mensajeError = null) }
    }

    /**
     * Reinicia el formulario a su estado inicial.
     */
    fun reiniciarFormulario() {
        _estado.value = UsuarioUIState()
    }

    // ==================== RECUPERACIÓN DE CONTRASEÑA ====================

    fun onEmailRecuperacionChange(value: String) {
        _estadoRecuperacion.update { it.copy(email = value) }
        validarEmailRecuperacion(value)
    }

    fun onTokenChange(value: String) {
        _estadoRecuperacion.update { it.copy(token = value) }
        if (value.isBlank()) {
            _estadoRecuperacion.update { it.copy(tokenError = "El token es obligatorio") }
        } else {
            _estadoRecuperacion.update { it.copy(tokenError = null) }
        }
    }

    fun onNuevaPasswordChange(value: String) {
        _estadoRecuperacion.update { it.copy(nuevaPassword = value) }
        validarNuevaPassword(value)
    }

    fun onConfirmarNuevaPasswordChange(value: String) {
        _estadoRecuperacion.update { it.copy(confirmarPassword = value) }
        validarConfirmarNuevaPassword(value)
    }

    private fun validarEmailRecuperacion(email: String) {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        val error = when {
            email.isBlank() -> "El email es obligatorio"
            !email.matches(emailPattern) -> "Email inválido"
            else -> null
        }
        _estadoRecuperacion.update { it.copy(emailError = error) }
    }

    private fun validarNuevaPassword(password: String) {
        val error = when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "Debe tener al menos 6 caracteres"
            else -> null
        }
        _estadoRecuperacion.update { it.copy(nuevaPasswordError = error) }

        if (_estadoRecuperacion.value.confirmarPassword.isNotBlank()) {
            validarConfirmarNuevaPassword(_estadoRecuperacion.value.confirmarPassword)
        }
    }

    private fun validarConfirmarNuevaPassword(confirmar: String) {
        val error = when {
            confirmar.isBlank() -> "Debes confirmar la contraseña"
            confirmar != _estadoRecuperacion.value.nuevaPassword -> "Las contraseñas no coinciden"
            else -> null
        }
        _estadoRecuperacion.update { it.copy(confirmarPasswordError = error) }
    }

    /**
     * Solicita recuperación de contraseña (paso 1).
     */
    fun solicitarRecuperacion(onSuccess: () -> Unit) {
        validarEmailRecuperacion(_estadoRecuperacion.value.email)

        if (!_estadoRecuperacion.value.paso1Valido()) {
            return
        }

        viewModelScope.launch {
            _estadoRecuperacion.update { it.copy(cargando = true, mensajeError = null) }

            val result = repository.recuperarPassword(_estadoRecuperacion.value.email)

            result.onSuccess { mensaje ->
                _estadoRecuperacion.update {
                    it.copy(
                        cargando = false,
                        paso = 2,
                        mensajeExito = mensaje
                    )
                }
                onSuccess()
            }.onFailure { error ->
                _estadoRecuperacion.update {
                    it.copy(
                        cargando = false,
                        mensajeError = error.message ?: "Error al solicitar recuperación"
                    )
                }
            }
        }
    }

    /**
     * Restablece la contraseña con el token (paso 2).
     */
    fun restablecerPassword(onSuccess: () -> Unit) {
        val s = _estadoRecuperacion.value
        onTokenChange(s.token)
        validarNuevaPassword(s.nuevaPassword)
        validarConfirmarNuevaPassword(s.confirmarPassword)

        if (!s.paso2Valido()) {
            return
        }

        viewModelScope.launch {
            _estadoRecuperacion.update { it.copy(cargando = true, mensajeError = null) }

            val result = repository.restablecerPassword(s.token, s.nuevaPassword)

            result.onSuccess { mensaje ->
                _estadoRecuperacion.update {
                    it.copy(
                        cargando = false,
                        mensajeExito = mensaje
                    )
                }
                onSuccess()
            }.onFailure { error ->
                _estadoRecuperacion.update {
                    it.copy(
                        cargando = false,
                        mensajeError = error.message ?: "Error al restablecer contraseña"
                    )
                }
            }
        }
    }

    fun limpiarEstadoRecuperacion() {
        _estadoRecuperacion.value = RecuperacionPasswordUIState()
    }
}
