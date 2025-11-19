package cl.duoc.medicalconsulta.model.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class CitaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pacienteNombre: String,
    val pacienteRut: String,
    val profesionalId: Long,
    val profesionalNombre: String,
    val especialidad: String,
    val fecha: String,
    val hora: String,
    val motivoConsulta: String,
    val timestamp: Long = System.currentTimeMillis()
)