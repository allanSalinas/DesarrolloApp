class BibliotecaViewModel(private val repository: BibliotecaRepository) {

    private val _catalogo = mutableStateOf<List<Libro>>(emptyList())
    val catalogo: State<List<Libro>> = _catalogo

    private val _estadoPrestamo = mutableStateOf<EstadoPrestamo>(EstadoPrestamo.Pendiente)
    val estadoPrestamo: State<EstadoPrestamo> = _estadoPrestamo

    private val _mensajeError = mutableStateOf<String?>(null)
    val mensajeError: State<String?> = _mensajeError

    private val _reporte = mutableStateOf<String>("")
    val reporte: State<String> = _reporte

    init {
        cargarCatalogo()
    }

    private fun cargarCatalogo() {
        viewModelScope.launch {
            try {
                _catalogo.value = repository.obtenerCatalogo()
            } catch (e: Exception) {
                _mensajeError.value = "Error al cargar catálogo: ${e.message}"
            }
        }
    }

    fun solicitarPrestamo(libro: Libro, usuario: Usuario, diasRetraso: Int = 0) {
        viewModelScope.launch {
            _estadoPrestamo.value = EstadoPrestamo.Pendiente
            _mensajeError.value = null

            val prestamo = Prestamo(
                libro = libro,
                usuario = usuario,
                fechaPrestamo = SimpleDateFormat("dd/MM/yyyy").format(Date()),
                diasRetraso = diasRetraso
            )

            _estadoPrestamo.value = repository.procesarPrestamo(prestamo)

            // Si hay error, actualizar mensaje
            if (_estadoPrestamo.value is EstadoPrestamo.Error) {
                val error = _estadoPrestamo.value as EstadoPrestamo.Error
                _mensajeError.value = error.mensaje
            }
        }
    }

    fun generarReporte() {
        _reporte.value = repository.generarReportePrestamos()
    }

    fun limpiarEstado() {
        _estadoPrestamo.value = EstadoPrestamo.Pendiente
        _mensajeError.value = null
        _reporte.value = ""
    }
}