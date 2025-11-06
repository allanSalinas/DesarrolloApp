package cl.duoc.app.model

data class RegisterUIState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val registrationSuccess: Boolean = false,
    val error: String? = null
)
