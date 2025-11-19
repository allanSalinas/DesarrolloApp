package cl.duoc.medicalconsulta.model.domain

data class Profesional(
    val id: Long,
    val nombre: String,
    val especialidad: String,
    val disponible: Boolean = true
)