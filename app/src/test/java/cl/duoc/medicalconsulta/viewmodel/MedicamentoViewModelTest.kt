package cl.duoc.medicalconsulta.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import cl.duoc.medicalconsulta.model.data.repository.remote.MedicamentoRepository
import cl.duoc.medicalconsulta.model.domain.Medicamento
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class MedicamentoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MedicamentoViewModel
    private lateinit var repository: MedicamentoRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = MedicamentoViewModel()

        // Inyectar el repository mockeado usando reflection
        val repositoryField = MedicamentoViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(viewModel, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `onBusquedaChange actualiza el estado correctamente`() = runTest {
        // Given
        val nuevaBusqueda = "aspirina"

        // When
        viewModel.onBusquedaChange(nuevaBusqueda)

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals(nuevaBusqueda, estado.busqueda)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `buscarMedicamentos con query vacio muestra error`() = runTest {
        // Given
        viewModel.onBusquedaChange("")

        // When
        viewModel.buscarMedicamentos()
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertNotNull(estado.error)
            assertEquals("Ingrese un nombre de medicamento", estado.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `buscarMedicamentos exitoso actualiza lista de medicamentos`() = runTest {
        // Given
        val query = "aspirina"
        val medicamentos = listOf(
            Medicamento(
                nombreComercial = "Aspirina",
                nombreGenerico = "Acido acetilsalicilico",
                fabricante = "Bayer",
                proposito = "Analgesico",
                indicaciones = "Dolor y fiebre",
                advertencias = "No usar en menores",
                ingredienteActivo = "ASA",
                viaAdministracion = "Oral"
            )
        )

        coEvery { repository.buscarMedicamentos(query) } returns medicamentos
        viewModel.onBusquedaChange(query)

        // When
        viewModel.buscarMedicamentos()
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals(1, estado.medicamentos.size)
            assertEquals("Aspirina", estado.medicamentos[0].nombreComercial)
            assertFalse(estado.cargando)
            assertEquals(null, estado.error)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.buscarMedicamentos(query) }
    }

    @Test
    fun `buscarMedicamentos sin resultados muestra mensaje de error`() = runTest {
        // Given
        val query = "medicamentoInexistente"
        coEvery { repository.buscarMedicamentos(query) } returns emptyList()
        viewModel.onBusquedaChange(query)

        // When
        viewModel.buscarMedicamentos()
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertTrue(estado.medicamentos.isEmpty())
            assertNotNull(estado.error)
            assertTrue(estado.error!!.contains("No se encontraron medicamentos"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `buscarMedicamentos con excepcion muestra error`() = runTest {
        // Given
        val query = "aspirina"
        val errorMessage = "Network error"
        coEvery { repository.buscarMedicamentos(query) } throws Exception(errorMessage)
        viewModel.onBusquedaChange(query)

        // When
        viewModel.buscarMedicamentos()
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertNotNull(estado.error)
            assertTrue(estado.error!!.contains("Error al buscar medicamentos"))
            assertFalse(estado.cargando)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `seleccionarMedicamento actualiza medicamento seleccionado`() = runTest {
        // Given
        val medicamento = Medicamento(
            nombreComercial = "Aspirina",
            nombreGenerico = "ASA",
            fabricante = "Bayer",
            proposito = "Analgesico",
            indicaciones = "Dolor",
            advertencias = "Precaucion",
            ingredienteActivo = "ASA",
            viaAdministracion = "Oral"
        )

        // When
        viewModel.seleccionarMedicamento(medicamento)

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals(medicamento, estado.medicamentoSeleccionado)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `limpiarBusqueda resetea el estado`() = runTest {
        // Given
        viewModel.onBusquedaChange("aspirina")

        // When
        viewModel.limpiarBusqueda()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.busqueda)
            assertTrue(estado.medicamentos.isEmpty())
            assertEquals(null, estado.error)
            assertEquals(null, estado.medicamentoSeleccionado)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `buscarMedicamentos muestra estado de carga`() = runTest {
        // Given
        val query = "aspirina"
        coEvery { repository.buscarMedicamentos(query) } coAnswers {
            kotlinx.coroutines.delay(1000)
            emptyList()
        }
        viewModel.onBusquedaChange(query)

        // When
        viewModel.buscarMedicamentos()

        // Then - Verificar que está cargando antes de que termine
        advanceTimeBy(500) // Avanzar solo la mitad del tiempo
        viewModel.estado.test {
            val estado = awaitItem()
            assertTrue(estado.cargando)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
