package cl.duoc.medicalconsulta.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.medicalconsulta.model.domain.Rol
import cl.duoc.medicalconsulta.utils.CameraHelper
import cl.duoc.medicalconsulta.viewmodel.UsuarioViewModel
import coil.compose.rememberAsyncImagePainter

/**
 * Pantalla de registro de nuevos usuarios.
 * Incluye formulario completo con validaciones y opción de foto de perfil.
 *
 * @param onRegistroExitoso Callback ejecutado cuando el registro es exitoso
 * @param onNavigateBack Callback para navegar hacia atrás
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: UsuarioViewModel = viewModel()
) {
    val context = LocalContext.current
    val estado by viewModel.estado.collectAsState()
    val scrollState = rememberScrollState()

    // Estado local para la foto
    var mostrarDialogoFoto by remember { mutableStateOf(false) }
    var mostrarPasswordActual by remember { mutableStateOf(false) }
    var mostrarPasswordConfirmar by remember { mutableStateOf(false) }

    // Helper para cámara
    val cameraHelper = remember { CameraHelper(context) }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para tomar foto con la cámara
    val tomarFotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            fotoUri?.let { uri ->
                viewModel.onFotoPerfilChange(uri.toString())
            }
        }
    }

    // Launcher para seleccionar de galería
    val seleccionarGaleriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            viewModel.onFotoPerfilChange(it.toString())
        }
    }

    // Launcher para permisos de cámara
    val permisosCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fotoUri = cameraHelper.createImageUri()
            fotoUri?.let { tomarFotoLauncher.launch(it) }
        }
    }

    // Efectos para mensajes
    LaunchedEffect(estado.mensajeExito) {
        estado.mensajeExito?.let {
            onRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { mostrarDialogoFoto = true },
                contentAlignment = Alignment.Center
            ) {
                if (estado.fotoPerfil != null) {
                    Image(
                        painter = rememberAsyncImagePainter(estado.fotoPerfil),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Añadir foto",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            TextButton(onClick = { mostrarDialogoFoto = true }) {
                Text("Cambiar foto de perfil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Username
            OutlinedTextField(
                value = estado.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = { Text("Nombre de usuario *") },
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                isError = estado.errores.username != null,
                supportingText = {
                    estado.errores.username?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Contraseña
            OutlinedTextField(
                value = estado.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña *") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { mostrarPasswordActual = !mostrarPasswordActual }) {
                        Icon(
                            if (mostrarPasswordActual) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (mostrarPasswordActual) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (mostrarPasswordActual) VisualTransformation.None else PasswordVisualTransformation(),
                isError = estado.errores.password != null,
                supportingText = {
                    estado.errores.password?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Confirmar Contraseña
            OutlinedTextField(
                value = estado.confirmarPassword,
                onValueChange = { viewModel.onConfirmarPasswordChange(it) },
                label = { Text("Confirmar contraseña *") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { mostrarPasswordConfirmar = !mostrarPasswordConfirmar }) {
                        Icon(
                            if (mostrarPasswordConfirmar) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (mostrarPasswordConfirmar) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (mostrarPasswordConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                isError = estado.errores.confirmarPassword != null,
                supportingText = {
                    estado.errores.confirmarPassword?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Nombre Completo
            OutlinedTextField(
                value = estado.nombreCompleto,
                onValueChange = { viewModel.onNombreCompletoChange(it) },
                label = { Text("Nombre completo *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = estado.errores.nombreCompleto != null,
                supportingText = {
                    estado.errores.nombreCompleto?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Email
            OutlinedTextField(
                value = estado.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email *") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = estado.errores.email != null,
                supportingText = {
                    estado.errores.email?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: RUT
            OutlinedTextField(
                value = estado.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT (12345678-9) *") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                isError = estado.errores.rut != null,
                supportingText = {
                    estado.errores.rut?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Teléfono
            OutlinedTextField(
                value = estado.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                isError = estado.errores.telefono != null,
                supportingText = {
                    estado.errores.telefono?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Rol
            Text(
                "Rol del usuario *",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Rol.values().forEach { rol ->
                    FilterChip(
                        selected = estado.rol == rol,
                        onClick = { viewModel.onRolChange(rol) },
                        label = { Text(rol.displayName()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de error general
            estado.mensajeError?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón de registro
            Button(
                onClick = { viewModel.registrar { _ -> onRegistroExitoso() } },
                enabled = !estado.cargando && estado.camposCompletos(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Usuario")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateBack) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }

    // Diálogo para seleccionar origen de foto
    if (mostrarDialogoFoto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoFoto = false },
            title = { Text("Seleccionar foto de perfil") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            mostrarDialogoFoto = false
                            // Verifica permisos de cámara
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) -> {
                                    fotoUri = cameraHelper.createImageUri()
                                    fotoUri?.let { tomarFotoLauncher.launch(it) }
                                }
                                else -> {
                                    permisosCameraLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar foto")
                    }

                    TextButton(
                        onClick = {
                            mostrarDialogoFoto = false
                            seleccionarGaleriaLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar de galería")
                    }

                    if (estado.fotoPerfil != null) {
                        TextButton(
                            onClick = {
                                mostrarDialogoFoto = false
                                viewModel.onFotoPerfilChange(null)
                                fotoUri = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar foto")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoFoto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
