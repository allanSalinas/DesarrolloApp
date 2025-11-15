package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.domain.RegisterUseCase // ¡CAMBIO! Ahora usamos el UseCase consolidado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// El data class RegisterUiState no necesita cambios
data class RegisterUiState(
    val nombreCompleto: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val tipoUsuario: String = "PACIENTE",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registroExitoso: Boolean = false
)

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() { // ¡CAMBIO! Se recibe el UseCase correcto

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // ... (el resto del ViewModel no necesita cambios, ya que la firma del `invoke` es la misma)

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombreCompleto = nombre, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun onTipoUsuarioChange(tipo: String) {
        _uiState.value = _uiState.value.copy(tipoUsuario = tipo, errorMessage = null)
    }

    fun registrarUsuario() {
        val currentState = _uiState.value

        if (currentState.password != currentState.confirmPassword) {
            _uiState.value = currentState.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = registerUseCase(
                nombreCompleto = currentState.nombreCompleto,
                email = currentState.email,
                password = currentState.password,
                tipoUsuario = currentState.tipoUsuario
            )

            result.fold(
                onSuccess = {
                    _uiState.value = currentState.copy(isLoading = false, registroExitoso = true, errorMessage = null)
                },
                onFailure = { exception ->
                    _uiState.value = currentState.copy(isLoading = false, errorMessage = exception.message ?: "Error desconocido")
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
