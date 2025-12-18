package cl.duoc.medicalconsulta.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import cl.duoc.medicalconsulta.model.data.entities.CitaEntity
import cl.duoc.medicalconsulta.model.data.entities.ProfesionalEntity
import cl.duoc.medicalconsulta.model.data.repository.CitaRepository
import cl.duoc.medicalconsulta.model.data.repository.ProfesionalRepository
import cl.duoc.medicalconsulta.model.domain.Profesional
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class CitaViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CitaViewModel
    private lateinit var citaRepository: CitaRepository
    private lateinit var profesionalRepository: ProfesionalRepository

    private val profesionalDummy = ProfesionalEntity(
        id = 1,
        nombre = "Dr. Juan Pérez",
        especialidad = "Cardiología",
        disponible = true
    )

    private val citaDummy = CitaEntity(
        id = 1,
        pacienteNombre = "María García",
        pacienteRut = "12345678-9",
        profesionalId = 1,
        profesionalNombre = "Dr. Juan Pérez",
        especialidad = "Cardiología",
        fecha = "15/12/2024",
        hora = "10:30",
        motivoConsulta = "Control de rutina"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        citaRepository = mockk(relaxed = true)
        profesionalRepository = mockk(relaxed = true)

        // Mock de profesionales disponibles
        coEvery { profesionalRepository.obtenerProfesionalesDisponibles() } returns flowOf(
            listOf(profesionalDummy)
        )

        // Mock de historial de citas
        coEvery { citaRepository.obtenerTodasCitas() } returns flowOf(listOf(citaDummy))

        viewModel = CitaViewModel(citaRepository, profesionalRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    fun `onNombreChange con valor vacio muestra error`() = runTest {
        // Given - When
        viewModel.onNombreChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.pacienteNombre)
            assertEquals("El nombre es obligatorio", estado.errores.pacienteNombre)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNombreChange con valor valido limpia error`() = runTest {
        // Given - When
        viewModel.onNombreChange("Juan Pérez")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Juan Pérez", estado.pacienteNombre)
            assertNull(estado.errores.pacienteNombre)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRutChange con formato invalido muestra error`() = runTest {
        // Given - When
        viewModel.onRutChange("123456789") // Sin guión

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("123456789", estado.pacienteRut)
            assertEquals("Formato de RUT inválido (ej: 12345678-9)", estado.errores.pacienteRut)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRutChange con formato valido limpia error`() = runTest {
        // Given - When
        viewModel.onRutChange("12345678-9")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("12345678-9", estado.pacienteRut)
            assertNull(estado.errores.pacienteRut)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRutChange con RUT vacio muestra error`() = runTest {
        // Given - When
        viewModel.onRutChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.pacienteRut)
            assertEquals("El RUT es obligatorio", estado.errores.pacienteRut)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFechaChange con formato invalido muestra error`() = runTest {
        // Given - When
        viewModel.onFechaChange("2024-12-15") // Formato incorrecto

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("2024-12-15", estado.fecha)
            assertEquals("Formato inválido (dd/mm/yyyy)", estado.errores.fecha)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFechaChange con formato valido limpia error`() = runTest {
        // Given - When
        viewModel.onFechaChange("15/12/2024")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("15/12/2024", estado.fecha)
            assertNull(estado.errores.fecha)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onHoraChange con formato invalido muestra error`() = runTest {
        // Given - When
        viewModel.onHoraChange("25:70") // Hora inválida

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("25:70", estado.hora)
            assertEquals("Formato inválido (HH:MM)", estado.errores.hora)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onHoraChange con formato valido limpia error`() = runTest {
        // Given - When
        viewModel.onHoraChange("14:30")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("14:30", estado.hora)
            assertNull(estado.errores.hora)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMotivoChange con valor vacio muestra error`() = runTest {
        // Given - When
        viewModel.onMotivoChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.motivoConsulta)
            assertEquals("El motivo de consulta es obligatorio", estado.errores.motivoConsulta)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMotivoChange con valor valido limpia error`() = runTest {
        // Given - When
        viewModel.onMotivoChange("Control de rutina")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Control de rutina", estado.motivoConsulta)
            assertNull(estado.errores.motivoConsulta)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onProfesionalSeleccionado actualiza profesional y limpia error`() = runTest {
        // Given
        val profesional = Profesional(
            id = 1,
            nombre = "Dr. Juan Pérez",
            especialidad = "Cardiología",
            disponible = true
        )

        // When
        viewModel.onProfesionalSeleccionado(profesional)

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals(profesional, estado.profesionalSeleccionado)
            assertNull(estado.errores.profesional)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE GUARDADO DE CITA ====================

    @Test
    fun `onAgendarCitaOActualizarCita con campos vacios muestra errores`() = runTest {
        // Given - todos los campos vacíos

        // When
        viewModel.onAgendarCitaOActualizarCita()
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertNotNull(estado.errores.pacienteNombre)
            assertNotNull(estado.errores.pacienteRut)
            assertNotNull(estado.errores.profesional)
            assertNotNull(estado.errores.fecha)
            assertNotNull(estado.errores.hora)
            assertNotNull(estado.errores.motivoConsulta)
            assertTrue(estado.errores.tieneErrores())
            cancelAndIgnoreRemainingEvents()
        }

        // Verificar que NO se llamó al repositorio
        coVerify(exactly = 0) { citaRepository.guardarCita(any()) }
    }

    @Test
    fun `onAgendarCitaOActualizarCita con datos validos guarda cita exitosamente`() = runTest {
        // Given - llenar todos los campos correctamente
        val profesional = Profesional(
            id = 1,
            nombre = "Dr. Juan Pérez",
            especialidad = "Cardiología",
            disponible = true
        )

        viewModel.onNombreChange("María García")
        viewModel.onRutChange("12345678-9")
        viewModel.onProfesionalSeleccionado(profesional)
        viewModel.onFechaChange("15/12/2024")
        viewModel.onHoraChange("10:30")
        viewModel.onMotivoChange("Control de rutina")

        coEvery { citaRepository.guardarCita(any()) } just Runs

        // When
        viewModel.onAgendarCitaOActualizarCita()
        advanceUntilIdle()

        // Then
        // Verificar que se llamó al repositorio
        coVerify { citaRepository.guardarCita(any()) }

        // Verificar mensaje de éxito
        viewModel.estado.test {
            val estado = awaitItem()
            assertTrue(estado.guardadoExitoso)
            assertFalse(estado.errores.tieneErrores())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAgendarCitaOActualizarCita guarda cita con datos correctos`() = runTest {
        // Given
        val profesional = Profesional(
            id = 1,
            nombre = "Dr. Juan Pérez",
            especialidad = "Cardiología",
            disponible = true
        )

        viewModel.onNombreChange("María García")
        viewModel.onRutChange("12345678-9")
        viewModel.onProfesionalSeleccionado(profesional)
        viewModel.onFechaChange("15/12/2024")
        viewModel.onHoraChange("10:30")
        viewModel.onMotivoChange("Control de rutina")

        val citaCapturada = slot<CitaEntity>()
        coEvery { citaRepository.guardarCita(capture(citaCapturada)) } just Runs

        // When
        viewModel.onAgendarCitaOActualizarCita()
        advanceUntilIdle()

        // Then
        val cita = citaCapturada.captured
        assertEquals("María García", cita.pacienteNombre)
        assertEquals("12345678-9", cita.pacienteRut)
        assertEquals(1, cita.profesionalId)
        assertEquals("Dr. Juan Pérez", cita.profesionalNombre)
        assertEquals("Cardiología", cita.especialidad)
        assertEquals("15/12/2024", cita.fecha)
        assertEquals("10:30", cita.hora)
        assertEquals("Control de rutina", cita.motivoConsulta)
    }

    // ==================== TESTS DE EDICIÓN ====================

    @Test
    fun `cargarCitaParaEdicion carga datos de cita existente`() = runTest {
        // Given
        val citaId = 1L
        coEvery { citaRepository.obtenerCitaPorId(citaId) } returns citaDummy

        // When
        viewModel.cargarCitaParaEdicion(citaId)
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("María García", estado.pacienteNombre)
            assertEquals("12345678-9", estado.pacienteRut)
            assertEquals("Dr. Juan Pérez", estado.profesionalSeleccionado?.nombre)
            assertEquals("15/12/2024", estado.fecha)
            assertEquals("10:30", estado.hora)
            assertEquals("Control de rutina", estado.motivoConsulta)
            assertFalse(estado.errores.tieneErrores())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { citaRepository.obtenerCitaPorId(citaId) }
    }

    @Test
    fun `cargarCitaParaEdicion con ID inexistente no actualiza estado`() = runTest {
        // Given
        val citaId = 999L
        coEvery { citaRepository.obtenerCitaPorId(citaId) } returns null

        // When
        viewModel.cargarCitaParaEdicion(citaId)
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            // El estado debe permanecer vacío
            assertEquals("", estado.pacienteNombre)
            assertEquals("", estado.pacienteRut)
            assertNull(estado.profesionalSeleccionado)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE ELIMINACIÓN ====================

    @Test
    fun `onEliminarCita elimina cita correctamente`() = runTest {
        // Given
        val citaId = 1L
        coEvery { citaRepository.eliminarCita(citaId) } just Runs

        // When
        viewModel.onEliminarCita(citaId)
        advanceUntilIdle()

        // Then
        coVerify { citaRepository.eliminarCita(citaId) }
    }

    // ==================== TESTS DE RESET ====================

    @Test
    fun `resetEstado limpia todos los campos`() = runTest {
        // Given - llenar algunos campos
        viewModel.onNombreChange("Juan")
        viewModel.onRutChange("12345678-9")

        // When
        viewModel.resetEstado()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.pacienteNombre)
            assertEquals("", estado.pacienteRut)
            assertEquals("", estado.fecha)
            assertEquals("", estado.hora)
            assertEquals("", estado.motivoConsulta)
            assertNull(estado.profesionalSeleccionado)
            assertFalse(estado.guardadoExitoso)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE CARGA DE DATOS ====================

    @Test
    fun `viewModel carga profesionales al inicializarse`() = runTest {
        // Given - ya configurado en setup

        // When - el ViewModel ya fue creado en setup
        advanceUntilIdle()

        // Then
        viewModel.profesionales.test {
            val profesionales = awaitItem()
            assertEquals(1, profesionales.size)
            assertEquals("Dr. Juan Pérez", profesionales[0].nombre)
            assertEquals("Cardiología", profesionales[0].especialidad)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { profesionalRepository.obtenerProfesionalesDisponibles() }
    }

    @Test
    fun `viewModel carga historial de citas al inicializarse`() = runTest {
        // Given - ya configurado en setup

        // When - el ViewModel ya fue creado en setup
        advanceUntilIdle()

        // Then
        viewModel.historialCitas.test {
            val citas = awaitItem()
            assertEquals(1, citas.size)
            assertEquals("María García", citas[0].pacienteNombre)
            assertEquals("Control de rutina", citas[0].motivoConsulta)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { citaRepository.obtenerTodasCitas() }
    }

    // ==================== TESTS DE MENSAJE DE ÉXITO ====================

    @Test
    fun `mensaje de guardado exitoso se oculta despues de 2 segundos`() = runTest {
        // Given
        val profesional = Profesional(
            id = 1,
            nombre = "Dr. Juan Pérez",
            especialidad = "Cardiología",
            disponible = true
        )

        viewModel.onNombreChange("María García")
        viewModel.onRutChange("12345678-9")
        viewModel.onProfesionalSeleccionado(profesional)
        viewModel.onFechaChange("15/12/2024")
        viewModel.onHoraChange("10:30")
        viewModel.onMotivoChange("Control de rutina")

        coEvery { citaRepository.guardarCita(any()) } just Runs

        // When
        viewModel.onAgendarCitaOActualizarCita()
        advanceTimeBy(1000) // 1 segundo

        // Then - aún debe estar en true
        viewModel.estado.test {
            val estado = awaitItem()
            assertTrue(estado.guardadoExitoso)
            cancelAndIgnoreRemainingEvents()
        }

        // When - avanzar otros 1500ms (total 2.5s)
        advanceTimeBy(1500)

        // Then - debe estar en false
        viewModel.estado.test {
            val estado = awaitItem()
            assertFalse(estado.guardadoExitoso)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
