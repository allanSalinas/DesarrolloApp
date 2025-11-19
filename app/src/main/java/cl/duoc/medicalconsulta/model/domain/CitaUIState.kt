package cl.duoc.medicalconsulta.model.domain

data class CitaUIState(
    val pacienteNombre: String = "",
    val pacienteRut: String = "",
    val profesionalSeleccionado: Profesional? = null,
    val fecha: String = "",
    val hora: String = "",
    val motivoConsulta: String = "",
    val errores: CitaErrores = CitaErrores(),
    val guardadoExitoso: Boolean = false
)