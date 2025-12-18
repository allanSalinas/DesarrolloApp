package cl.duoc.medicalconsulta.model.data.dao

import androidx.room.*
import cl.duoc.medicalconsulta.model.data.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para operaciones CRUD sobre la tabla de usuarios.
 * Proporciona métodos para interactuar con la base de datos local.
 */
@Dao
interface UsuarioDao {

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Si el usuario ya existe (por ID), se reemplaza.
     *
     * @param usuario Usuario a insertar
     * @return ID del usuario insertado
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: UsuarioEntity): Long

    /**
     * Inserta múltiples usuarios en la base de datos.
     *
     * @param usuarios Lista de usuarios a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usuarios: List<UsuarioEntity>)

    /**
     * Actualiza un usuario existente.
     *
     * @param usuario Usuario con los datos actualizados
     * @return Número de filas actualizadas
     */
    @Update
    suspend fun update(usuario: UsuarioEntity): Int

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param usuario Usuario a eliminar
     * @return Número de filas eliminadas
     */
    @Delete
    suspend fun delete(usuario: UsuarioEntity): Int

    /**
     * Obtiene todos los usuarios de la base de datos.
     * Retorna un Flow para observar cambios en tiempo real.
     *
     * @return Flow con la lista de todos los usuarios
     */
    @Query("SELECT * FROM usuarios ORDER BY nombre_completo ASC")
    fun getAll(): Flow<List<UsuarioEntity>>

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario a buscar
     * @return Usuario encontrado o null
     */
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UsuarioEntity?

    /**
     * Obtiene un usuario por su ID como Flow (observable).
     *
     * @param id ID del usuario a buscar
     * @return Flow con el usuario o null
     */
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun getByIdFlow(id: Long): Flow<UsuarioEntity?>

    /**
     * Obtiene un usuario por su nombre de usuario.
     * Útil para verificar duplicados o búsqueda en login.
     *
     * @param username Nombre de usuario a buscar
     * @return Usuario encontrado o null
     */
    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): UsuarioEntity?

    /**
     * Obtiene un usuario por su email.
     * Útil para recuperación de contraseña.
     *
     * @param email Email del usuario
     * @return Usuario encontrado o null
     */
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UsuarioEntity?

    /**
     * Obtiene usuarios por rol.
     *
     * @param rol Rol a filtrar (PACIENTE, MEDICO, etc.)
     * @return Flow con la lista de usuarios con ese rol
     */
    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY nombre_completo ASC")
    fun getByRol(rol: String): Flow<List<UsuarioEntity>>

    /**
     * Obtiene solo usuarios activos.
     *
     * @return Flow con la lista de usuarios activos
     */
    @Query("SELECT * FROM usuarios WHERE activo = 1 ORDER BY nombre_completo ASC")
    fun getUsuariosActivos(): Flow<List<UsuarioEntity>>

    /**
     * Verifica si existe un usuario con el username dado.
     *
     * @param username Nombre de usuario a verificar
     * @return true si existe, false si no
     */
    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE username = :username LIMIT 1)")
    suspend fun existeUsername(username: String): Boolean

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE email = :email LIMIT 1)")
    suspend fun existeEmail(email: String): Boolean

    /**
     * Verifica si existe un usuario con el RUT dado.
     *
     * @param rut RUT a verificar
     * @return true si existe, false si no
     */
    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE rut = :rut LIMIT 1)")
    suspend fun existeRut(rut: String): Boolean

    /**
     * Actualiza la foto de perfil de un usuario.
     *
     * @param id ID del usuario
     * @param fotoPerfil Nueva URL/path de la foto
     * @return Número de filas actualizadas
     */
    @Query("UPDATE usuarios SET foto_perfil = :fotoPerfil, ultima_actualizacion = :timestamp WHERE id = :id")
    suspend fun updateFotoPerfil(id: Long, fotoPerfil: String, timestamp: Long = System.currentTimeMillis()): Int

    /**
     * Cambia el estado activo de un usuario.
     *
     * @param id ID del usuario
     * @param activo Nuevo estado
     * @return Número de filas actualizadas
     */
    @Query("UPDATE usuarios SET activo = :activo, ultima_actualizacion = :timestamp WHERE id = :id")
    suspend fun updateEstadoActivo(id: Long, activo: Boolean, timestamp: Long = System.currentTimeMillis()): Int

    /**
     * Elimina todos los usuarios de la base de datos.
     * Útil para cerrar sesión o limpiar cache.
     */
    @Query("DELETE FROM usuarios")
    suspend fun deleteAll()
}
