class LibroFisico(
    override val titulo: String,
    override val autor: String,
    override val precioBase: Double,
    override val diasPrestamo: Int,
    val esReferencia: Boolean
) : Libro(titulo, autor, precioBase, diasPrestamo) {

    override fun puedePrestarse(): Boolean {
        return !esReferencia && super.puedePrestarse()
    }

    override fun descripcion(): String {
        val tipo = if (esReferencia) "Referencia" else "Físico"
        return "${super.descripcion()} - [$tipo]"
    }
}