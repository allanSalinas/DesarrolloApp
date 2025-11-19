package cl.duoc.app.model.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,
    val professional: String,
    val date: Long,         // guardamos fecha en timestamp
    val time: String        // ejemplo "10:30"
)