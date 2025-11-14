package cl.duoc.app.model.domain

import cl.duoc.app.model.data.entities.UserEntity
import cl.duoc.app.model.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import java.security.MessageDigest

/**
 * Caso de Uso para la lógica de negocio del registro de usuario.
 * Contiene todas las validaciones y la lógica de creación de la entidad.
 */
class RegisterUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(nombreCompleto: String, email: String, password: String, tipoUsuario: String): Result<Unit> {
        // 1. Validación de campos obligatorios
        if (nombreCompleto.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Todos los campos son obligatorios"))
        }

        // 2. Validación de formato de email
        if (!isValidEmail(email)) {
            return Result.failure(Exception("El formato del correo electrónico no es válido"))
        }

        // 3. Validación de longitud de contraseña (Criterio HU-01)
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        // 4. Comprobar si el usuario ya existe en la base de datos
        val existingUser = userRepository.findUserByEmail(email).firstOrNull()
        if (existingUser != null) {
            return Result.failure(Exception("El correo electrónico ya está registrado"))
        }

        // 5. Encriptar la contraseña (¡Buena práctica!)
        val encryptedPassword = encryptPassword(password)

        // Si todas las validaciones pasan, se crea y se inserta el usuario
        val newUser = UserEntity(
            nombreCompleto = nombreCompleto.trim(),
            email = email.trim().lowercase(),
            password = encryptedPassword,
            tipoUsuario = tipoUsuario
            // Los demás campos como 'rut', 'telefono', etc., se pueden añadir aquí si es necesario
        )

        return try {
            userRepository.registerUser(newUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    private fun encryptPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
