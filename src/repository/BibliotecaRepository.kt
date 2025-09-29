class BibliotecaRepository {

    private val catalogoLibros = mutableListOf<Libro>()
    private val prestamos = mutableListOf<Prestamo>()

    init {
        inicializarCatalogo()
    }

    private fun inicializarCatalogo() {
        catalogoLibros.addAll(
            listOf(
                LibroFisico("Estructuras de Datos", "Goodrich", 12990.0, 7, false),
                LibroFisico("Diccionario Enciclopédico", "Varios", 15990.0, 0, true),
                LibroDigital("Programación en Kotlin", "JetBrains", 9990.0, 10, true),
                LibroDigital("Algoritmos Básicos", "Cormen", 11990.0, 10, false)
            )
        )
    }

    suspend fun obtenerCatalogo(): List<Libro> {
        delay(500) // Simula carga asíncrona
        return catalogoLibros.toList()
    }

    suspend fun procesarPrestamo(prestamo: Prestamo): EstadoPrestamo {
        return try {
            // Validaciones
            if (!prestamo.libro.puedePrestarse()) {
                throw IllegalArgumentException("Este libro no puede prestarse")
            }

            if (prestamo.libro.precioBase < 0) {
                throw IllegalArgumentException("Precio inválido")
            }

            // Simula procesamiento asíncrono
            delay(3000)

            prestamos.add(prestamo.copy(estado = EstadoPrestamo.EnPrestamo))
            EstadoPrestamo.EnPrestamo

        } catch (e: Exception) {
            EstadoPrestamo.Error(e.message ?: "Error desconocido")
        }
    }

    fun obtenerPrestamos(): List<Prestamo> = prestamos.toList()

    fun obtenerLibrosPrestados(): List<Libro> {
        return prestamos
            .filter { it.estado == EstadoPrestamo.EnPrestamo }
            .map { it.libro }
    }

    fun generarReportePrestamos(): String {
        val totalPrestamos = prestamos.size
        val totalIngresos = prestamos.sumOf { it.calcularTotal() }
        val librosPrestados = obtenerLibrosPrestados().size

        return """
            === REPORTE DE PRÉSTAMOS ===
            Total préstamos: $totalPrestamos
            Libros actualmente prestados: $librosPrestados
            Ingresos totales: $${"%.2f".format(totalIngresos)}
        """.trimIndent()
    }
}