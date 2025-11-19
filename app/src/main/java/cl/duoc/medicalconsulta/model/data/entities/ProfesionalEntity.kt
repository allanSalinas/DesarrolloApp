package cl.duoc.medicalconsulta.model.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profesionales")
data class ProfesionalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val especialidad: String,
    val disponible: Boolean = true
)