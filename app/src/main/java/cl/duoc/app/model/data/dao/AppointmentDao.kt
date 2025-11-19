package cl.duoc.app.model.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.app.model.data.entities.AppointmentEntity

@Dao
interface AppointmentDao {

    @Insert
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointments WHERE userId = :userId")
    suspend fun getAppointmentsByUser(userId: Int): List<AppointmentEntity>
}