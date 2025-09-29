fun main() = runBlocking {
    val repository = BibliotecaRepository()
    val viewModel = BibliotecaViewModel(repository)

    println("=== SISTEMA DE GESTIÓN BIBLIOTECA BOOKSMART ===\n")

    // Mostrar catálogo
    println("📚 CATÁLOGO DE LIBROS DISPONIBLES:")
    viewModel.catalogo.value.forEachIndexed { index, libro ->
        println("${index + 1}. ${libro.descripcion()}")
    }

    println("\n" + "=".repeat(50))

    // Simular préstamos
    val usuarioEstudiante = Usuario("Juan Pérez", TipoUsuario.ESTUDIANTE)
    val usuarioDocente = Usuario("María García", TipoUsuario.DOCENTE)

    // Préstamo 1: Libro físico normal
    val libro1 = viewModel.catalogo.value[0]
    println("\n📋 SOLICITANDO PRÉSTAMO 1:")
    println("Libro: ${libro1.titulo}")
    println("Usuario: ${usuarioEstudiante.nombre} (${usuarioEstudiante.tipo})")

    viewModel.solicitarPrestamo(libro1, usuarioEstudiante)

    // Esperar a que termine el procesamiento
    while (viewModel.estadoPrestamo.value == EstadoPrestamo.Pendiente) {
        delay(100)
        print(".")
    }

    when (viewModel.estadoPrestamo.value) {
        is EstadoPrestamo.EnPrestamo -> println("\n✅ Préstamo aprobado exitosamente")
        is EstadoPrestamo.Error -> println("\n❌ Error: ${viewModel.mensajeError.value}")
        else -> {}
    }

    println("\n" + "=".repeat(50))

    // Préstamo 2: Libro de referencia (debe fallar)
    val libro2 = viewModel.catalogo.value[1]
    println("\n📋 SOLICITANDO PRÉSTAMO 2 (LIBRO DE REFERENCIA):")
    println("Libro: ${libro2.titulo}")

    viewModel.solicitarPrestamo(libro2, usuarioDocente)

    while (viewModel.estadoPrestamo.value == EstadoPrestamo.Pendiente) {
        delay(100)
        print(".")
    }

    when (viewModel.estadoPrestamo.value) {
        is EstadoPrestamo.EnPrestamo -> println("\n✅ Préstamo aprobado exitosamente")
        is EstadoPrestamo.Error -> println("\n❌ Error: ${viewModel.mensajeError.value}")
        else -> {}
    }

    println("\n" + "=".repeat(50))

    // Generar reporte
    println("\n📊 GENERANDO REPORTE DE PRÉSTAMOS...")
    viewModel.generarReporte()
    delay(1000)
    println(viewModel.reporte.value)

    println("\n🎯 OPERACIÓN FINALIZADA")
}