package cl.duoc.app.model.repository

import cl.duoc.app.model.data.UserDao
import cl.duoc.app.model.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: UserEntity) {
        userDao.insert(user)
    }

    fun findUserByEmail(email: String): Flow<UserEntity?> {
        return userDao.findByEmail(email)
    }
}
