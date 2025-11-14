package cl.duoc.mediReserva.model.domain

import cl.duoc.mediReserva.model.data.entities.UsuarioEntity
import cl.duoc.mediReserva.model.data.repository.UsuarioRepository
import java.security.MessageDigest

class RegisterUseCase(private val repository: UsuarioRepository) {

    suspend operator fun invoke(
        nombreCompleto: String,
        email: String,
        password: String,
        tipoUsuario: String,
        telefono: String? = null,
        fechaNacimiento: String? = null,
        rut: String? = null
    ): Result<Long> {

        // Validaciones
        if (nombreCompleto.isBlank()) {
            return Result.failure(Exception("El nombre es obligatorio"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(Exception("Email inválido"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        if (tipoUsuario !in listOf("PACIENTE", "MEDICO")) {
            return Result.failure(Exception("Tipo de usuario inválido"))
        }

        // Verificar si el email ya existe
        val emailExiste = repository.existeEmail(email)
        if (emailExiste) {
            return Result.failure(Exception("El email ya está registrado"))
        }

        // Encriptar contraseña
        val passwordEncriptada = encriptarPassword(password)

        // Crear entidad de usuario
        val usuario = UsuarioEntity(
            nombreCompleto = nombreCompleto.trim(),
            email = email.trim().lowercase(),
            password = passwordEncriptada,
            tipoUsuario = tipoUsuario,
            telefono = telefono,
            fechaNacimiento = fechaNacimiento,
            rut = rut
        )

        // Registrar en el repositorio
        return repository.registrarUsuario(usuario)
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