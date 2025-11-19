package cl.duoc.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.validation.FormValidator
import cl.duoc.app.model.domain.RegisterUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var nameError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    fun registrar(
        onSuccess: () -> Unit,
        onErrorGeneral: (String) -> Unit
    ) {
        // 1. Validaciones locales
        val nameResult = FormValidator.validateName(name)
        val emailResult = FormValidator.validateEmail(email)
        val passwordResult = FormValidator.validatePassword(password)

        nameError = nameResult.errorMessage
        emailError = emailResult.errorMessage
        passwordError = passwordResult.errorMessage

        // Si alguna validación falla, no continuamos
        if (!nameResult.isValid || !emailResult.isValid || !passwordResult.isValid) {
            onErrorGeneral("Por favor corrija los errores del formulario")
            return
        }

        // 2. Si todo OK, llamamos al caso de uso
        viewModelScope.launch {
            registerUseCase(name.trim(), email.trim(), password, "PACIENTE")
                .onSuccess { onSuccess() }
                .onFailure { e -> onErrorGeneral(e.message ?: "Error al registrar") }
        }
    }
}