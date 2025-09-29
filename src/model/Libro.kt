open class Libro(
    open val titulo: String,
    open val autor: String,
    open val precioBase: Double,
    open val diasPrestamo: Int
) {
    open fun costoFinal(): Double = precioBase

    open fun puedePrestarse(): Boolean = diasPrestamo > 0

    open fun descripcion(): String = "$titulo - $autor ($$precioBase)"
}