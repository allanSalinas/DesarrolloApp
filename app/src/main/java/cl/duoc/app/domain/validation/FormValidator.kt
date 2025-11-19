package cl.duoc.app.domain.validation

import android.util.Patterns

object FormValidator {

    fun validateNotEmpty(value: String, fieldName: String = "Campo"): ValidationResult {
        if (value.trim().isEmpty()) {
            return ValidationResult(false, "$fieldName obligatorio")
        }
        return ValidationResult(true)
    }

    fun validateEmail(email: String): ValidationResult {
        if (email.trim().isEmpty()) {
            return ValidationResult(false, "Correo obligatorio")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(false, "Ingrese correo válido")
        }
        return ValidationResult(true)
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(false, "Contraseña obligatoria")
        }
        if (password.length < 6) {
            return ValidationResult(false, "La contraseña debe tener al menos 6 caracteres")
        }
        return ValidationResult(true)
    }

    fun validateName(name: String): ValidationResult {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult(false, "Nombre obligatorio")
        }

        // solo letras y espacios básicos
        val regex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")
        if (!regex.matches(trimmed)) {
            return ValidationResult(false, "Ingrese un nombre válido")
        }

        return ValidationResult(true)
    }
}