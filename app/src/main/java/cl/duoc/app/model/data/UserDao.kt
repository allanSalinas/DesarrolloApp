package cl.duoc.app.model.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.app.model.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email")
    fun findByEmail(email: String): Flow<UserEntity?>
}
