package cl.duoc.mediReserva.model.data.dao

import androidx.room.*
import cl.duoc.mediReserva.model.data.entities.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity)

    @Delete
    suspend fun deleteUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE id = :usuarioId")
    suspend fun getUsuarioById(usuarioId: Int): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUsuarioByEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE tipo_usuario = :tipoUsuario")
    suspend fun getUsuariosByTipo(tipoUsuario: String): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): List<UsuarioEntity>

    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email")
    suspend fun existeEmail(email: String): Int
}