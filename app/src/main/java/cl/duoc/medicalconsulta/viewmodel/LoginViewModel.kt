package cl.duoc.medicalconsulta.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.medicalconsulta.model.data.repository.UsuarioRepository
import cl.duoc.medicalconsulta.model.domain.LoginUIState
import cl.duoc.medicalconsulta.model.domain.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de inicio de sesión.
 * Maneja la autenticación de usuarios y el estado de la sesión.
 *
 * @property context Contexto de la aplicación para acceder al repositorio
 */
class LoginViewModel(private val context: Context) : ViewModel() {

    private val repository = UsuarioRepository(context)

    // Estado de la UI de login
    private val _estado = MutableStateFlow(LoginUIState())
    val estado: StateFlow<LoginUIState> = _estado.asStateFlow()

    // Usuario autenticado actualmente
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun onUsernameChange(v: String) = _estado.update {
        it.copy(username = v, error = null)
    }

    fun onPasswordChange(v: String) = _estado.update {
        it.copy(password = v, error = null)
    }

    /**
     * Autentica al usuario con las credenciales proporcionadas.
     * Utiliza el UsuarioRepository para validar contra el backend.
     *
     * @param onSuccess Callback a ejecutar cuando el login es exitoso
     */
    fun autenticar(onSuccess: (Usuario) -> Unit) {
        val s = _estado.value

        // Validaciones básicas
        if (s.username.isBlank() || s.password.isBlank()) {
            _estado.update {
                it.copy(error = "Por favor, completa todos los campos")
            }
            return
        }

        viewModelScope.launch {
            // Inicia el estado de carga
            _estado.update { it.copy(cargando = true, error = null) }

            // Intenta autenticar a través del repositorio
            val result = repository.login(s.username, s.password)

            result.onSuccess { usuario ->
                // Autenticación exitosa
                _usuarioActual.value = usuario
                _estado.update { it.copy(cargando = false, error = null) }
                onSuccess(usuario)
            }.onFailure { error ->
                // Error en autenticación
                _estado.update {
                    it.copy(
                        cargando = false,
                        error = error.message ?: "Usuario o contraseña incorrectos"
                    )
                }
            }
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     * Limpia los datos locales y resetea el estado.
     */
    fun cerrarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            _usuarioActual.value = null
            _estado.value = LoginUIState()
            onSuccess()
        }
    }

    /**
     * Verifica si hay una sesión activa guardada localmente.
     * Útil para auto-login al abrir la app.
     */
    fun verificarSesionActiva(onUsuarioEncontrado: (Usuario) -> Unit) {
        viewModelScope.launch {
            // Intenta obtener el usuario guardado localmente
            // En una implementación real, verificarías un token o ID guardado
            // Por ahora, dejamos esta funcionalidad para futuras mejoras
        }
    }

    /**
     * Limpia el mensaje de error.
     */
    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }
}