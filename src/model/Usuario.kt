enum class TipoUsuario(val descuento: Double) {
    ESTUDIANTE(0.10),
    DOCENTE(0.15),
    EXTERNO(0.0)
}

data class Usuario(
    val nombre: String,
    val tipo: TipoUsuario
)