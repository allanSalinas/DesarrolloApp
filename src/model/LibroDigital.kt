class LibroDigital(
    override val titulo: String,
    override val autor: String,
    override val precioBase: Double,
    override val diasPrestamo: Int,
    val tieneDRM: Boolean
) : Libro(titulo, autor, precioBase, diasPrestamo) {

    override fun costoFinal(): Double {
        // Libros con DRM pueden tener un costo adicional o diferente
        return if (tieneDRM) precioBase * 0.9 else precioBase
    }

    override fun descripcion(): String {
        val drm = if (tieneDRM) "Con DRM" else "Sin DRM"
        return "${super.descripcion()} - [Digital - $drm]"
    }
}