package cl.duoc.mediReserva.model.data.repository

import cl.duoc.mediReserva.model.data.dao.UsuarioDao
import cl.duoc.mediReserva.model.data.entities.UsuarioEntity

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrarUsuario(usuario: UsuarioEntity): Result<Long> {
        return try {
            // Verificar si el email ya existe
            val emailExiste = usuarioDao.existeEmail(usuario.email) > 0
            if (emailExiste) {
                Result.failure(Exception("El email ya está registrado"))
            } else {
                val id = usuarioDao.insertUsuario(usuario)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<UsuarioEntity> {
        return try {
            val usuario = usuarioDao.login(email, password)
            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerUsuarioPorId(id: Int): Result<UsuarioEntity> {
        return try {
            val usuario = usuarioDao.getUsuarioById(id)
            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarUsuario(usuario: UsuarioEntity): Result<Unit> {
        return try {
            usuarioDao.updateUsuario(usuario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun existeEmail(email: String): Boolean {
        return try {
            usuarioDao.existeEmail(email) > 0
        } catch (e: Exception) {
            false
        }
    }
}