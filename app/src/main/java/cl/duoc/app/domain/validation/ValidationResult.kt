package cl.duoc.app.domain.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)