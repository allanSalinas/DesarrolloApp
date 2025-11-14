package cl.duoc.app.model.domain

import cl.duoc.app.model.data.entities.UserEntity
import cl.duoc.app.model.data.repository.UserRepository
import java.security.MessageDigest

class LoginUseCase(private val repository: UserRepository) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<UserEntity> {

        // Validaciones
        if (email.isBlank()) {
            return Result.failure(Exception("El email es obligatorio"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es obligatoria"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(Exception("Email inválido"))
        }

        // Encriptar contraseña para comparar
        val passwordEncriptada = encriptarPassword(password)

        // Intentar login
        return repository.login(
            email = email.trim().lowercase(),
            password = passwordEncriptada
        )
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    private fun encriptarPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}