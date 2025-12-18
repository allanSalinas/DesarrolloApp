package cl.duoc.medicalconsulta.model.domain

data class Cita(
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
