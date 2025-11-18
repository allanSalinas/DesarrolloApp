package cl.duoc.app.model.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.app.model.data.entities.AppointmentEntity

@Dao
interface AppointmentDao {

    @Insert
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointments WHERE userId = :userId")
    suspend fun getAppointmentsByUser(userId: Int): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Int): AppointmentEntity?

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateAppointmentStatus(id: Int, status: String)

    @Query("SELECT * FROM appointments WHERE userId = :userId AND status = 'Programada'")
    suspend fun getActiveAppointmentsByUser(userId: Int): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE userId = :userId AND status != 'Programada'")
    suspend fun getInactiveAppointmentsByUser(userId: Int): List<AppointmentEntity>

    // 👈 NUEVO
    @Query("SELECT * FROM appointments WHERE professionalId = :professionalId")
    suspend fun getAppointmentsForProfessional(professionalId: Int): List<AppointmentEntity>
}