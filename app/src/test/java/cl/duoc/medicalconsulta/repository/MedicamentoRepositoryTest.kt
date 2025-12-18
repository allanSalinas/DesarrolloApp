package cl.duoc.medicalconsulta.repository

import android.util.Log
import cl.duoc.medicalconsulta.model.data.repository.remote.MedicamentoRepository
import cl.duoc.medicalconsulta.network.OpenFdaClient
import cl.duoc.medicalconsulta.network.api.MedicamentoApiService
import cl.duoc.medicalconsulta.network.dto.FdaResponseDto
import cl.duoc.medicalconsulta.network.dto.MedicamentoDto
import cl.duoc.medicalconsulta.network.dto.OpenFdaDto
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class MedicamentoRepositoryTest {

    private lateinit var repository: MedicamentoRepository
    private lateinit var apiService: MedicamentoApiService

    @Before
    fun setup() {
        // Mock Android Log to avoid "Method e in android.util.Log not mocked" error
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        apiService = mockk()

        // Mockear el OpenFdaClient para que devuelva nuestro apiService mockeado
        mockkObject(OpenFdaClient)
        every { OpenFdaClient.medicamentoApiService } returns apiService

        repository = MedicamentoRepository()
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkObject(OpenFdaClient)
        clearAllMocks()
    }

    @Test
    fun `buscarMedicamentos retorna lista vacia cuando la respuesta falla`() = runTest {
        // Given
        val query = "aspirina"
        val searchQuery = "openfda.brand_name:$query OR openfda.generic_name:$query"
        val response = Response.error<FdaResponseDto>(404, mockk(relaxed = true))

        coEvery { apiService.buscarMedicamentos(searchQuery, 10) } returns response

        // When
        val result = repository.buscarMedicamentos(query)

        // Then
        assertTrue(result.isEmpty())
        coVerify { apiService.buscarMedicamentos(searchQuery, 10) }
    }

    @Test
    fun `buscarMedicamentos retorna lista vacia cuando ocurre excepcion`() = runTest {
        // Given
        val query = "aspirina"
        val searchQuery = "openfda.brand_name:$query OR openfda.generic_name:$query"

        coEvery { apiService.buscarMedicamentos(searchQuery, 10) } throws Exception("Network error")

        // When
        val result = repository.buscarMedicamentos(query)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `buscarMedicamentos mapea correctamente los DTOs a dominio`() = runTest {
        // Given
        val query = "aspirina"
        val searchQuery = "openfda.brand_name:$query OR openfda.generic_name:$query"

        val medicamentoDto = MedicamentoDto(
            openfda = OpenFdaDto(
                brandName = listOf("Aspirina"),
                genericName = listOf("Acido acetilsalicilico"),
                manufacturerName = listOf("Bayer"),
                productType = listOf("HUMAN OTC DRUG"),
                route = listOf("ORAL")
            ),
            purpose = listOf("Analgesico y antipiretico"),
            indicationsAndUsage = listOf("Para el alivio temporal de dolores menores y fiebre"),
            warnings = listOf("No usar en ninos menores de 12 anos sin consultar al medico"),
            activeIngredient = listOf("Acido acetilsalicilico 500mg")
        )

        val fdaResponse = FdaResponseDto(results = listOf(medicamentoDto))
        val response = Response.success(fdaResponse)

        coEvery { apiService.buscarMedicamentos(searchQuery, 10) } returns response

        // When
        val result = repository.buscarMedicamentos(query)

        // Then
        assertEquals(1, result.size)
        assertEquals("Aspirina", result[0].nombreComercial)
        assertEquals("Acido acetilsalicilico", result[0].nombreGenerico)
        assertEquals("Bayer", result[0].fabricante)
        assertEquals("Analgesico y antipiretico", result[0].proposito)
        assertEquals("ORAL", result[0].viaAdministracion)
        coVerify { apiService.buscarMedicamentos(searchQuery, 10) }
    }

    @Test
    fun `buscarMedicamentos maneja DTOs sin openfda`() = runTest {
        // Given
        val query = "test"
        val searchQuery = "openfda.brand_name:$query OR openfda.generic_name:$query"

        val medicamentoDto = MedicamentoDto(
            openfda = null,
            purpose = listOf("Test purpose"),
            indicationsAndUsage = listOf("Test indications"),
            warnings = listOf("Test warnings"),
            activeIngredient = listOf("Test ingredient")
        )

        val fdaResponse = FdaResponseDto(results = listOf(medicamentoDto))
        val response = Response.success(fdaResponse)

        coEvery { apiService.buscarMedicamentos(searchQuery, 10) } returns response

        // When
        val result = repository.buscarMedicamentos(query)

        // Then
        assertEquals(1, result.size)
        assertEquals("No disponible", result[0].nombreComercial)
        assertEquals("No disponible", result[0].nombreGenerico)
        assertEquals("No disponible", result[0].fabricante)
    }

    @Test
    fun `buscarMedicamentos limita texto largo en indicaciones y advertencias`() = runTest {
        // Given
        val query = "test"
        val searchQuery = "openfda.brand_name:$query OR openfda.generic_name:$query"

        val textoLargo = "A".repeat(500) // Texto de 500 caracteres

        val medicamentoDto = MedicamentoDto(
            openfda = OpenFdaDto(
                brandName = listOf("Test"),
                genericName = listOf("Test Generic"),
                manufacturerName = listOf("Test Manufacturer"),
                productType = null,
                route = listOf("ORAL")
            ),
            purpose = listOf("Test"),
            indicationsAndUsage = listOf(textoLargo),
            warnings = listOf(textoLargo),
            activeIngredient = null
        )

        val fdaResponse = FdaResponseDto(results = listOf(medicamentoDto))
        val response = Response.success(fdaResponse)

        coEvery { apiService.buscarMedicamentos(searchQuery, 10) } returns response

        // When
        val result = repository.buscarMedicamentos(query)

        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].indicaciones.length <= 300)
        assertTrue(result[0].advertencias.length <= 300)
    }

    @Test
    fun `buscarMedicamentos filtra medicamentos que no se pueden mapear`() = runTest {
        // Given
        val query = "test"
        val searchQuery = "openfda.brand_name:$query OR openfda.generic_name:$query"

        val medicamentoValido = MedicamentoDto(
            openfda = OpenFdaDto(
                brandName = listOf("Valid"),
                genericName = listOf("Valid Generic"),
                manufacturerName = listOf("Valid Manufacturer"),
                productType = null,
                route = listOf("ORAL")
            ),
            purpose = listOf("Test"),
            indicationsAndUsage = listOf("Test"),
            warnings = listOf("Test"),
            activeIngredient = listOf("Test")
        )

        val fdaResponse = FdaResponseDto(results = listOf(medicamentoValido))
        val response = Response.success(fdaResponse)

        coEvery { apiService.buscarMedicamentos(searchQuery, 10) } returns response

        // When
        val result = repository.buscarMedicamentos(query)

        // Then
        assertTrue(result.isNotEmpty())
        assertEquals("Valid", result[0].nombreComercial)
    }
}
