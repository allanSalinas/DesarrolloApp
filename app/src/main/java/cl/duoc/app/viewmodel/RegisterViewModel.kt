package cl.duoc.mediReserva.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mediReserva.model.data.config.AppDatabase
import cl.duoc.mediReserva.model.data.repository.UsuarioRepository
import cl.duoc.mediReserva.model.domain.RegistrarUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val nombreCompleto: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val tipoUsuario: String = "PACIENTE",
    val telefono: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registroExitoso: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = UsuarioRepository(database.usuarioDao())
    private val registrarUsuarioUseCase = RegistrarUsuarioUseCase(repository)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(
            nombreCompleto = nombre,
            errorMessage = null
        )
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            errorMessage = null
        )
    }

    fun onTipoUsuarioChange(tipo: String) {
        _uiState.value = _uiState.value.copy(
            tipoUsuario = tipo,
            errorMessage = null
        )
    }

    fun onTelefonoChange(telefono: String) {
        _uiState.value = _uiState.value.copy(
            telefono = telefono,
            errorMessage = null
        )
    }

    fun registrarUsuario() {
        val currentState = _uiState.value

        // Validación de contraseñas coincidentes
        if (currentState.password != currentState.confirmPassword) {
            _uiState.value = currentState.copy(
                errorMessage = "Las contraseñas no coinciden"
            )
            return
        }

        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = registrarUsuarioUseCase(
                nombreCompleto = currentState.nombreCompleto,
                email = currentState.email,
                password = currentState.password,
                tipoUsuario = currentState.tipoUsuario,
                telefono = currentState.telefono.ifBlank { null }
            )

            result.fold(
                onSuccess = {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        registroExitoso = true,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al registrar usuario"
                    )
                }
            )
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetRegistroExitoso() {
        _uiState.value = _uiState.value.copy(registroExitoso = false)
    }
}