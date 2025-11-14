package cl.duoc.app.model.domain

import cl.duoc.app.model.data.entities.UserEntity
import cl.duoc.app.model.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Tests unitarios para RegistrarUsuarioUseCase
 * Verifica las validaciones y lógica de negocio del registro de usuarios
 */
class RegistrarUsuarioUseCaseTest {

    @Mock
    private lateinit var repository: UserRepository

    private lateinit var registrarUsuarioUseCase: RegisterUIState

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        registrarUsuarioUseCase = RegisterUIState(repository)
    }

    /**
     * Test: Validación de nombre completo obligatorio
     */
    @Test
    fun `registrar usuario con nombre vacio debe fallar`() = runBlocking {
        // Given
        val nombre = ""
        val email = "test@example.com"
        val password = "password123"
        val tipoUsuario = "PACIENTE"

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre es obligatorio", result.exceptionOrNull()?.message)
    }

    /**
     * Test: Validación de email inválido
     */
    @Test
    fun `registrar usuario con email invalido debe fallar`() = runBlocking {
        // Given
        val nombre = "Juan Pérez"
        val email = "emailinvalido"
        val password = "password123"
        val tipoUsuario = "PACIENTE"

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals("Email inválido", result.exceptionOrNull()?.message)
    }

    /**
     * Test: Validación de contraseña corta
     */
    @Test
    fun `registrar usuario con contrasena corta debe fallar`() = runBlocking {
        // Given
        val nombre = "Juan Pérez"
        val email = "juan@example.com"
        val password = "12345" // Menos de 6 caracteres
        val tipoUsuario = "PACIENTE"

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals(
            "La contraseña debe tener al menos 6 caracteres",
            result.exceptionOrNull()?.message
        )
    }

    /**
     * Test: Validación de tipo de usuario inválido
     */
    @Test
    fun `registrar usuario con tipo invalido debe fallar`() = runBlocking {
        // Given
        val nombre = "Juan Pérez"
        val email = "juan@example.com"
        val password = "password123"
        val tipoUsuario = "ADMIN" // Tipo no válido

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals("Tipo de usuario inválido", result.exceptionOrNull()?.message)
    }

    /**
     * Test: Email duplicado debe fallar
     */
    @Test
    fun `registrar usuario con email duplicado debe fallar`() = runBlocking {
        // Given
        val nombre = "Juan Pérez"
        val email = "juan@example.com"
        val password = "password123"
        val tipoUsuario = "PACIENTE"

        // Mock: Email ya existe
        `when`(repository.existeEmail(email.lowercase())).thenReturn(true)

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals("El email ya está registrado", result.exceptionOrNull()?.message)
    }

    /**
     * Test: Registro exitoso con datos válidos
     */
    @Test
    fun `registrar usuario con datos validos debe tener exito`() = runBlocking {
        // Given
        val nombre = "Juan Pérez"
        val email = "juan@example.com"
        val password = "password123"
        val tipoUsuario = "PACIENTE"
        val expectedId = 1L

        // Mock: Email no existe
        `when`(repository.existeEmail(email.lowercase())).thenReturn(false)

        // Mock: Inserción exitosa
        val usuario = UserEntity(
            nombreCompleto = nombre.trim(),
            email = email.trim().lowercase(),
            password = anyString(), // La contraseña estará encriptada
            tipoUsuario = tipoUsuario
        )
        `when`(repository.registrarUsuario(any())).thenReturn(Result.success(expectedId))

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedId, result.getOrNull())

        // Verificar que se llamó al repository
        verify(repository).existeEmail(email.lowercase())
        verify(repository).registrarUsuario(any())
    }

    /**
     * Test: Email se convierte a lowercase
     */
    @Test
    fun `registrar usuario debe convertir email a lowercase`() = runBlocking {
        // Given
        val nombre = "Juan Pérez"
        val email = "JUAN@EXAMPLE.COM"
        val password = "password123"
        val tipoUsuario = "PACIENTE"

        // Mock
        `when`(repository.existeEmail(email.lowercase())).thenReturn(false)
        `when`(repository.registrarUsuario(any())).thenReturn(Result.success(1L))

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isSuccess)

        // Verificar que se usó email en lowercase
        verify(repository).existeEmail("juan@example.com")
    }

    /**
     * Test: Contraseña se encripta correctamente
     */
    @Test
    fun `registrar usuario debe encriptar contrasena`() = runBlocking {
        // Given
        val nombre = "Juan Pérez"
        val email = "juan@example.com"
        val password = "password123"
        val tipoUsuario = "PACIENTE"

        // Mock
        `when`(repository.existeEmail(email.lowercase())).thenReturn(false)
        `when`(repository.registrarUsuario(any())).thenReturn(Result.success(1L))

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isSuccess)

        // Capturar el usuario que se pasó al repository
        val captor = argumentCaptor<UserEntity>()
        verify(repository).registrarUsuario(captor.capture())

        // Verificar que la contraseña no es la original (está encriptada)
        assertNotEquals(password, captor.value.password)
        assertTrue(captor.value.password.length == 64) // SHA-256 produce 64 caracteres hex
    }

    /**
     * Test: Nombre se trimea correctamente
     */
    @Test
    fun `registrar usuario debe trimear nombre completo`() = runBlocking {
        // Given
        val nombre = "  Juan Pérez  "
        val email = "juan@example.com"
        val password = "password123"
        val tipoUsuario = "PACIENTE"

        // Mock
        `when`(repository.existeEmail(email.lowercase())).thenReturn(false)
        `when`(repository.registrarUsuario(any())).thenReturn(Result.success(1L))

        // When
        val result = registrarUsuarioUseCase(
            nombreCompleto = nombre,
            email = email,
            password = password,
            tipoUsuario = tipoUsuario
        )

        // Then
        assertTrue(result.isSuccess)

        // Capturar el usuario
        val captor = argumentCaptor<UserEntity>()
        verify(repository).registrarUsuario(captor.capture())

        // Verificar que el nombre fue trimeado
        assertEquals("Juan Pérez", captor.value.nombreCompleto)
    }
}

// Helper function para mockito
inline fun <reified T : Any> argumentCaptor(): org.mockito.ArgumentCaptor<T> =
    org.mockito.ArgumentCaptor.forClass(T::class.java)