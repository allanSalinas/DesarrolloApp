package cl.duoc.medicalconsulta.model.domain

/**
 * Enumeración que representa los diferentes roles de usuario en el sistema.
 *
 * Roles disponibles:
 * - PACIENTE: Usuario que puede agendar citas y consultar su historial médico
 * - MEDICO: Profesional que atiende consultas y gestiona pacientes
 * - ADMINISTRADOR: Usuario con permisos completos sobre el sistema
 * - RECEPCIONISTA: Usuario que gestiona citas y recepción de pacientes
 */
enum class Rol {
    PACIENTE,
    MEDICO,
    ADMINISTRADOR,
    RECEPCIONISTA;

    /**
     * Devuelve el nombre del rol en formato legible para mostrar en la UI
     */
    fun displayName(): String = when (this) {
        PACIENTE -> "Paciente"
        MEDICO -> "Médico"
        ADMINISTRADOR -> "Administrador"
        RECEPCIONISTA -> "Recepcionista"
    }

    companion object {
        /**
         * Convierte un string a un Rol, útil para parsing de datos
         * @param value String con el nombre del rol
         * @return Rol correspondiente o null si no es válido
         */
        fun fromString(value: String?): Rol? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
