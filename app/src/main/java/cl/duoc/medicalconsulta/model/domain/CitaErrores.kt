package cl.duoc.medicalconsulta.model.domain

data class CitaErrores(
    val pacienteNombre: String? = null,
    val pacienteRut: String? = null,
    val profesional: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val motivoConsulta: String? = null
) {
    fun tieneErrores(): Boolean =
        pacienteNombre != null ||
                pacienteRut != null ||
                profesional != null ||
                fecha != null ||
                hora != null ||
                motivoConsulta != null
}