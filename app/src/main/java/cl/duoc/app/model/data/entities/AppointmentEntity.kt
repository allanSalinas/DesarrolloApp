package cl.duoc.app.model.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val professionalId: Int,   // 👈 NUEVO
    val professional: String,
    val date: Long,
    val time: String,
    val status: String = "Programada"
)