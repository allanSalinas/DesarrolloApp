package cl.duoc.medicalconsulta.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import cl.duoc.medicalconsulta.model.data.repository.UsuarioRepository
import cl.duoc.medicalconsulta.model.domain.Rol
import cl.duoc.medicalconsulta.model.domain.Usuario
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class UsuarioViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: UsuarioViewModel
    private lateinit var context: Context
    private lateinit var repository: UsuarioRepository

    private val usuarioDummy = Usuario(
        id = 1,
        username = "juan.perez",
        nombreCompleto = "Juan Pérez",
        email = "juan@example.com",
        rut = "12345678-5",
        rol = Rol.PACIENTE,
        fotoPerfil = null,
        telefono = "987654321",
        activo = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
        repository = mockk(relaxed = true)

        viewModel = UsuarioViewModel(context)

        // Inyectar el repository mockeado usando reflection
        val repositoryField = UsuarioViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(viewModel, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ==================== TESTS DE VALIDACIÓN USERNAME ====================

    @Test
    fun `onUsernameChange con valor vacio muestra error`() = runTest {
        // Given - When
        viewModel.onUsernameChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.username)
            assertEquals("El nombre de usuario es obligatorio", estado.errores.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUsernameChange con menos de 4 caracteres muestra error`() = runTest {
        // Given - When
        viewModel.onUsernameChange("abc")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("abc", estado.username)
            assertEquals("Debe tener al menos 4 caracteres", estado.errores.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUsernameChange con mas de 20 caracteres muestra error`() = runTest {
        // Given - When
        viewModel.onUsernameChange("a".repeat(21))

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Máximo 20 caracteres", estado.errores.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUsernameChange con caracteres invalidos muestra error`() = runTest {
        // Given - When
        viewModel.onUsernameChange("juan pérez@")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Solo se permiten letras, números, punto y guión bajo", estado.errores.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUsernameChange con valor valido limpia error`() = runTest {
        // Given - When
        viewModel.onUsernameChange("juan.perez_123")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("juan.perez_123", estado.username)
            assertNull(estado.errores.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE VALIDACIÓN PASSWORD ====================

    @Test
    fun `onPasswordChange con valor vacio muestra error`() = runTest {
        // Given - When
        viewModel.onPasswordChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.password)
            assertEquals("La contraseña es obligatoria", estado.errores.password)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPasswordChange con menos de 6 caracteres muestra error`() = runTest {
        // Given - When
        viewModel.onPasswordChange("12345")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Debe tener al menos 6 caracteres", estado.errores.password)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPasswordChange con mas de 50 caracteres muestra error`() = runTest {
        // Given - When
        viewModel.onPasswordChange("a".repeat(51))

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Máximo 50 caracteres", estado.errores.password)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPasswordChange con valor valido limpia error`() = runTest {
        // Given - When
        viewModel.onPasswordChange("password123")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("password123", estado.password)
            assertNull(estado.errores.password)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE VALIDACIÓN CONFIRMAR PASSWORD ====================

    @Test
    fun `onConfirmarPasswordChange con passwords diferentes muestra error`() = runTest {
        // Given
        viewModel.onPasswordChange("password123")

        // When
        viewModel.onConfirmarPasswordChange("password456")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("password456", estado.confirmarPassword)
            assertEquals("Las contraseñas no coinciden", estado.errores.confirmarPassword)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmarPasswordChange con passwords iguales limpia error`() = runTest {
        // Given
        viewModel.onPasswordChange("password123")

        // When
        viewModel.onConfirmarPasswordChange("password123")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("password123", estado.confirmarPassword)
            assertNull(estado.errores.confirmarPassword)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE VALIDACIÓN NOMBRE COMPLETO ====================

    @Test
    fun `onNombreCompletoChange con valor vacio muestra error`() = runTest {
        // Given - When
        viewModel.onNombreCompletoChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.nombreCompleto)
            assertEquals("El nombre completo es obligatorio", estado.errores.nombreCompleto)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNombreCompletoChange con menos de 3 caracteres muestra error`() = runTest {
        // Given - When
        viewModel.onNombreCompletoChange("AB")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Debe tener al menos 3 caracteres", estado.errores.nombreCompleto)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNombreCompletoChange con mas de 100 caracteres muestra error`() = runTest {
        // Given - When
        viewModel.onNombreCompletoChange("a".repeat(101))

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Máximo 100 caracteres", estado.errores.nombreCompleto)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNombreCompletoChange con numeros muestra error`() = runTest {
        // Given - When
        viewModel.onNombreCompletoChange("Juan 123")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Solo se permiten letras y espacios", estado.errores.nombreCompleto)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNombreCompletoChange con valor valido limpia error`() = runTest {
        // Given - When
        viewModel.onNombreCompletoChange("Juan Pérez García")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Juan Pérez García", estado.nombreCompleto)
            assertNull(estado.errores.nombreCompleto)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE VALIDACIÓN EMAIL ====================

    @Test
    fun `onEmailChange con valor vacio muestra error`() = runTest {
        // Given - When
        viewModel.onEmailChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.email)
            assertEquals("El email es obligatorio", estado.errores.email)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEmailChange con formato invalido muestra error`() = runTest {
        // Given - When
        viewModel.onEmailChange("correo_invalido")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Email inválido", estado.errores.email)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEmailChange con formato valido limpia error`() = runTest {
        // Given - When
        viewModel.onEmailChange("juan@example.com")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("juan@example.com", estado.email)
            assertNull(estado.errores.email)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE VALIDACIÓN RUT ====================

    @Test
    fun `onRutChange con valor vacio muestra error`() = runTest {
        // Given - When
        viewModel.onRutChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.rut)
            assertEquals("El RUT es obligatorio", estado.errores.rut)
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
            assertEquals("Formato inválido (ej: 12345678-9)", estado.errores.rut)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRutChange con digito verificador invalido muestra error`() = runTest {
        // Given - When
        viewModel.onRutChange("12345678-0") // DV incorrecto

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("RUT inválido", estado.errores.rut)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRutChange con RUT valido limpia error`() = runTest {
        // Given - When
        viewModel.onRutChange("12345678-5") // DV correcto

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("12345678-5", estado.rut)
            assertNull(estado.errores.rut)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE VALIDACIÓN TELÉFONO ====================

    @Test
    fun `onTelefonoChange con valor vacio no muestra error`() = runTest {
        // Given - When
        viewModel.onTelefonoChange("")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("", estado.telefono)
            assertNull(estado.errores.telefono) // Teléfono es opcional
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onTelefonoChange con formato invalido muestra error`() = runTest {
        // Given - When
        viewModel.onTelefonoChange("123") // Menos de 8 dígitos

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("Teléfono inválido (8-15 dígitos)", estado.errores.telefono)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onTelefonoChange con formato valido limpia error`() = runTest {
        // Given - When
        viewModel.onTelefonoChange("987654321")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("987654321", estado.telefono)
            assertNull(estado.errores.telefono)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE CAMBIO DE ROL ====================

    @Test
    fun `onRolChange actualiza el rol correctamente`() = runTest {
        // Given - When
        viewModel.onRolChange(Rol.MEDICO)

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals(Rol.MEDICO, estado.rol)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE FOTO DE PERFIL ====================

    @Test
    fun `onFotoPerfilChange actualiza la foto correctamente`() = runTest {
        // Given - When
        viewModel.onFotoPerfilChange("foto_base64_string")

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("foto_base64_string", estado.fotoPerfil)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFotoPerfilChange con null limpia la foto`() = runTest {
        // Given - When
        viewModel.onFotoPerfilChange(null)

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertNull(estado.fotoPerfil)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE REGISTRO ====================

    @Test
    fun `registrar con campos vacios muestra error`() = runTest {
        // Given - campos vacíos
        var successCalled = false

        // When
        viewModel.registrar { successCalled = true }
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertNotNull(estado.mensajeError)
            assertTrue(estado.mensajeError!!.contains("completa todos los campos"))
            assertFalse(successCalled)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { repository.registro(any(), any()) }
    }

    @Test
    fun `registrar con datos validos llama al repositorio`() = runTest {
        // Given - llenar todos los campos correctamente
        viewModel.onUsernameChange("juan.perez")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmarPasswordChange("password123")
        viewModel.onNombreCompletoChange("Juan Pérez")
        viewModel.onEmailChange("juan@example.com")
        viewModel.onRutChange("12345678-5")
        viewModel.onRolChange(Rol.PACIENTE)

        coEvery { repository.registro(any(), any()) } returns Result.success(usuarioDummy)

        var successCalled = false
        var usuarioRecibido: Usuario? = null

        // When
        viewModel.registrar { usuario ->
            successCalled = true
            usuarioRecibido = usuario
        }
        advanceUntilIdle()

        // Then
        assertTrue(successCalled)
        assertEquals(usuarioDummy, usuarioRecibido)

        viewModel.estado.test {
            val estado = awaitItem()
            assertNotNull(estado.mensajeExito)
            assertTrue(estado.mensajeExito!!.contains("registrado exitosamente"))
            assertFalse(estado.cargando)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.registro(any(), "password123") }
    }

    @Test
    fun `registrar con error del repositorio muestra mensaje de error`() = runTest {
        // Given
        viewModel.onUsernameChange("juan.perez")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmarPasswordChange("password123")
        viewModel.onNombreCompletoChange("Juan Pérez")
        viewModel.onEmailChange("juan@example.com")
        viewModel.onRutChange("12345678-5")
        viewModel.onRolChange(Rol.PACIENTE)

        val errorMessage = "Usuario ya existe"
        coEvery { repository.registro(any(), any()) } returns Result.failure(Exception(errorMessage))

        var successCalled = false

        // When
        viewModel.registrar { successCalled = true }
        advanceUntilIdle()

        // Then
        assertFalse(successCalled)

        viewModel.estado.test {
            val estado = awaitItem()
            assertNotNull(estado.mensajeError)
            assertEquals(errorMessage, estado.mensajeError)
            assertFalse(estado.cargando)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== TESTS DE CARGA DE USUARIO ====================

    @Test
    fun `cargarUsuarioParaEdicion carga datos exitosamente`() = runTest {
        // Given
        val usuarioId = 1L
        coEvery { repository.getUsuarioById(usuarioId) } returns Result.success(usuarioDummy)

        // When
        viewModel.cargarUsuarioParaEdicion(usuarioId)
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertEquals("juan.perez", estado.username)
            assertEquals("Juan Pérez", estado.nombreCompleto)
            assertEquals("juan@example.com", estado.email)
            assertEquals("12345678-5", estado.rut)
            assertEquals(Rol.PACIENTE, estado.rol)
            assertTrue(estado.modoEdicion)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.getUsuarioById(usuarioId) }
    }

    @Test
    fun `cargarUsuarioParaEdicion con error muestra mensaje`() = runTest {
        // Given
        val usuarioId = 1L
        val errorMessage = "Usuario no encontrado"
        coEvery { repository.getUsuarioById(usuarioId) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.cargarUsuarioParaEdicion(usuarioId)
        advanceUntilIdle()

        // Then
        viewModel.estado.test {
            val estado = awaitItem()
            assertNotNull(estado.mensajeError)
            assertEquals(errorMessage, estado.mensajeError)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
