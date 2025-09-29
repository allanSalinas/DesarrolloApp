data class Prestamo(
    val libro: Libro,
    val usuario: Usuario,
    val fechaPrestamo: String,
    val diasRetraso: Int = 0,
    var estado: EstadoPrestamo = EstadoPrestamo.Pendiente
) {
    fun calcularMulta(): Double {
        return if (diasRetraso > 0) libro.precioBase * 0.05 * diasRetraso else 0.0
    }

    fun calcularDescuento(): Double {
        return libro.costoFinal() * usuario.tipo.descuento
    }

    fun calcularTotal(): Double {
        return libro.costoFinal() - calcularDescuento() + calcularMulta()
    }
}