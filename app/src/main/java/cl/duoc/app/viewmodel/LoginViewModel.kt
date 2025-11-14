package cl.duoc.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.data.entities.UserEntity
import cl.duoc.app.model.data.repository.UserRepository
import cl.duoc.app.model.domain.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginExitoso: Boolean = false,
    val usuarioLogueado: UserEntity? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao())
    private val loginUseCase = LoginUseCase(repository)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

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

    fun login() {
        val currentState = _uiState.value

        // Validaciones básicas
        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "El email es obligatorio"
            )
            return
        }

        if (currentState.password.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "La contraseña es obligatoria"
            )
            return
        }

        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = loginUseCase(
                email = currentState.email,
                password = currentState.password
            )

            result.fold(
                onSuccess = { usuario ->
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        loginExitoso = true,
                        usuarioLogueado = usuario,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al iniciar sesión"
                    )
                }
            )
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetLoginExitoso() {
        _uiState.value = _uiState.value.copy(loginExitoso = false)
    }
}