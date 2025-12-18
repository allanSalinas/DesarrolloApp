package cl.duoc.medicalconsulta.model.data.repository

import android.content.Context
import android.net.Uri
import cl.duoc.medicalconsulta.model.data.config.AppDatabase
import cl.duoc.medicalconsulta.model.data.dao.UsuarioDao
import cl.duoc.medicalconsulta.model.data.entities.UsuarioEntity
import cl.duoc.medicalconsulta.model.domain.Rol
import cl.duoc.medicalconsulta.model.domain.Usuario
import cl.duoc.medicalconsulta.network.api.UsuarioApiService
import cl.duoc.medicalconsulta.network.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Repositorio que combina operaciones locales (Room) y remotas (Retrofit) para usuarios.
 * Implementa el patrón Repository para abstraer la lógica de acceso a datos.
 *
 * @property context Contexto de la aplicación
 */
class UsuarioRepository(private val context: Context) {

    // DAO local para Room Database
    private val usuarioDao: UsuarioDao = AppDatabase.getDatabase(context).usuarioDao()

    // API Service para Retrofit
    private val apiService: UsuarioApiService

    init {
        // Configuración de Retrofit (ajustar BASE_URL según tu backend)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // Android emulator localhost
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(UsuarioApiService::class.java)
    }

    // ==================== AUTENTICACIÓN ====================

    /**
     * Inicia sesión con username y password.
     * Primero intenta autenticar remotamente, si tiene éxito guarda localmente.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Result con el Usuario autenticado o error
     */
    suspend fun login(username: String, password: String): Result<Usuario> {
        return try {
            // Intenta login remoto
            val request = LoginRequest(username, password)
            val response = apiService.login(request)

            if (response.isSuccessful && response.body()?.success == true) {
                val loginResponse = response.body()!!
                val usuario = loginResponse.usuario?.toDomain()

                if (usuario != null) {
                    // Guarda el usuario localmente
                    usuarioDao.insert(UsuarioEntity.fromDomain(usuario))
                    Result.success(usuario)
                } else {
                    Result.failure(Exception("Datos de usuario inválidos"))
                }
            } else {
                val mensaje = response.body()?.mensaje ?: "Error de autenticación"
                Result.failure(Exception(mensaje))
            }
        } catch (e: Exception) {
            // Si falla la conexión, intenta login local (modo offline)
            try {
                val usuarioEntity = usuarioDao.getByUsername(username)
                if (usuarioEntity != null) {
                    // NOTA: En producción, NO guardar contraseñas en local
                    // Aquí solo se verifica que el usuario existe
                    Result.success(usuarioEntity.toDomain())
                } else {
                    Result.failure(Exception("Usuario o contraseña incorrectos"))
                }
            } catch (localException: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Primero intenta registro remoto, luego guarda localmente.
     *
     * @param usuario Datos del nuevo usuario
     * @param password Contraseña del usuario
     * @return Result con el Usuario creado o error
     */
    suspend fun registro(usuario: Usuario, password: String): Result<Usuario> {
        return try {
            // Valida que no exista el username localmente primero
            if (usuarioDao.existeUsername(usuario.username)) {
                return Result.failure(Exception("El nombre de usuario ya está en uso"))
            }

            // Intenta registro remoto
            val usuarioDto = UsuarioDto.fromDomain(usuario, password)
            val response = apiService.registro(usuarioDto)

            if (response.isSuccessful && response.body() != null) {
                val usuarioCreado = response.body()!!.toDomain()
                // Guarda localmente
                usuarioDao.insert(UsuarioEntity.fromDomain(usuarioCreado))
                Result.success(usuarioCreado)
            } else {
                Result.failure(Exception("Error al registrar usuario"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     * Elimina los datos locales del usuario.
     */
    suspend fun logout() {
        // En una implementación real, podrías mantener algunos datos
        // Aquí eliminamos todos los usuarios locales
        usuarioDao.deleteAll()
    }

    // ==================== RECUPERACIÓN DE CONTRASEÑA ====================

    /**
     * Solicita recuperación de contraseña enviando un email.
     *
     * @param email Email del usuario
     * @return Result con mensaje de éxito o error
     */
    suspend fun recuperarPassword(email: String): Result<String> {
        return try {
            val request = RecuperacionPasswordRequest(email)
            val response = apiService.recuperarPassword(request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.mensaje)
            } else {
                Result.failure(Exception(response.body()?.mensaje ?: "Error al solicitar recuperación"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Restablece la contraseña usando el token recibido por email.
     *
     * @param token Token de recuperación
     * @param nuevaPassword Nueva contraseña
     * @return Result con mensaje de éxito o error
     */
    suspend fun restablecerPassword(token: String, nuevaPassword: String): Result<String> {
        return try {
            val request = RestablecerPasswordRequest(token, nuevaPassword)
            val response = apiService.restablecerPassword(request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.mensaje)
            } else {
                Result.failure(Exception(response.body()?.mensaje ?: "Error al restablecer contraseña"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // ==================== GESTIÓN DE PERFIL ====================

    /**
     * Obtiene un usuario por su ID.
     * Primero busca localmente, si no existe intenta remotamente.
     *
     * @param id ID del usuario
     * @return Result con el Usuario o error
     */
    suspend fun getUsuarioById(id: Long): Result<Usuario> {
        return try {
            // Primero intenta local
            val local = usuarioDao.getById(id)
            if (local != null) {
                return Result.success(local.toDomain())
            }

            // Si no está local, intenta remoto
            val response = apiService.getUsuario(id)
            if (response.isSuccessful && response.body() != null) {
                val usuario = response.body()!!.toDomain()
                // Guarda en cache local
                usuarioDao.insert(UsuarioEntity.fromDomain(usuario))
                Result.success(usuario)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    /**
     * Obtiene un usuario por su ID como Flow (observable).
     *
     * @param id ID del usuario
     * @return Flow con el Usuario o null
     */
    fun getUsuarioByIdFlow(id: Long): Flow<Usuario?> {
        return usuarioDao.getByIdFlow(id).map { it?.toDomain() }
    }

    /**
     * Actualiza los datos de un usuario.
     *
     * @param usuario Usuario con los datos actualizados
     * @return Result con el Usuario actualizado o error
     */
    suspend fun actualizarPerfil(usuario: Usuario): Result<Usuario> {
        return try {
            // Actualiza remotamente
            val usuarioDto = UsuarioDto.fromDomain(usuario)
            val response = apiService.actualizarUsuario(usuario.id, usuarioDto)

            if (response.isSuccessful && response.body() != null) {
                val usuarioActualizado = response.body()!!.toDomain()
                // Actualiza localmente
                usuarioDao.update(UsuarioEntity.fromDomain(usuarioActualizado))
                Result.success(usuarioActualizado)
            } else {
                Result.failure(Exception("Error al actualizar perfil"))
            }
        } catch (e: Exception) {
            // Si falla remoto, al menos actualiza local
            try {
                usuarioDao.update(UsuarioEntity.fromDomain(usuario))
                Result.success(usuario)
            } catch (localError: Exception) {
                Result.failure(Exception("Error: ${e.message}"))
            }
        }
    }

    /**
     * Actualiza la foto de perfil de un usuario.
     *
     * @param usuarioId ID del usuario
     * @param fotoUri URI de la foto seleccionada
     * @return Result con el Usuario actualizado o error
     */
    suspend fun actualizarFoto(usuarioId: Long, fotoUri: Uri): Result<Usuario> {
        return try {
            // Prepara el archivo para upload
            val file = File(fotoUri.path ?: "")
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("foto", file.name, requestFile)

            // Sube la foto al servidor
            val response = apiService.actualizarFotoPerfil(usuarioId, body)

            if (response.isSuccessful && response.body() != null) {
                val usuarioActualizado = response.body()!!.toDomain()
                // Actualiza localmente
                usuarioDao.update(UsuarioEntity.fromDomain(usuarioActualizado))
                Result.success(usuarioActualizado)
            } else {
                Result.failure(Exception("Error al actualizar foto"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    /**
     * Actualiza solo la URL/path de la foto de perfil localmente.
     *
     * @param usuarioId ID del usuario
     * @param fotoPath Path local de la foto
     * @return Result con éxito o error
     */
    suspend fun actualizarFotoLocal(usuarioId: Long, fotoPath: String): Result<Unit> {
        return try {
            usuarioDao.updateFotoPerfil(usuarioId, fotoPath)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar foto: ${e.message}"))
        }
    }

    // ==================== CONSULTAS LOCALES ====================

    /**
     * Obtiene todos los usuarios guardados localmente.
     *
     * @return Flow con la lista de usuarios
     */
    fun getAllUsuariosLocal(): Flow<List<Usuario>> {
        return usuarioDao.getAll().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    /**
     * Obtiene usuarios por rol guardados localmente.
     *
     * @param rol Rol a filtrar
     * @return Flow con la lista de usuarios
     */
    fun getUsuariosByRolLocal(rol: Rol): Flow<List<Usuario>> {
        return usuarioDao.getByRol(rol.name).map { lista ->
            lista.map { it.toDomain() }
        }
    }

    /**
     * Verifica si un username ya está en uso.
     *
     * @param username Username a verificar
     * @return true si existe, false si está disponible
     */
    suspend fun existeUsername(username: String): Boolean {
        return try {
            // Primero verifica localmente
            if (usuarioDao.existeUsername(username)) {
                return true
            }

            // Si no existe local, verifica remoto
            val response = apiService.verificarUsername(username)
            response.isSuccessful && response.body()?.get("existe") == true
        } catch (e: Exception) {
            // Si hay error de red, solo verifica local
            usuarioDao.existeUsername(username)
        }
    }

    /**
     * Verifica si un email ya está en uso.
     *
     * @param email Email a verificar
     * @return true si existe, false si está disponible
     */
    suspend fun existeEmail(email: String): Boolean {
        return try {
            // Primero verifica localmente
            if (usuarioDao.existeEmail(email)) {
                return true
            }

            // Si no existe local, verifica remoto
            val response = apiService.verificarEmail(email)
            response.isSuccessful && response.body()?.get("existe") == true
        } catch (e: Exception) {
            // Si hay error de red, solo verifica local
            usuarioDao.existeEmail(email)
        }
    }

    /**
     * Verifica si un RUT ya está en uso (solo local).
     *
     * @param rut RUT a verificar
     * @return true si existe, false si está disponible
     */
    suspend fun existeRut(rut: String): Boolean {
        return usuarioDao.existeRut(rut)
    }
}
