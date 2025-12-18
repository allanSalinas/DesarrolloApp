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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.medicalconsulta.model.domain.Rol
import cl.duoc.medicalconsulta.model.domain.Usuario
import cl.duoc.medicalconsulta.utils.CameraHelper
import cl.duoc.medicalconsulta.viewmodel.LoginViewModel
import cl.duoc.medicalconsulta.viewmodel.UsuarioViewModel
import coil.compose.rememberAsyncImagePainter

/**
 * Pantalla de perfil de usuario.
 * Muestra los datos del usuario autenticado y permite editar perfil y cambiar foto.
 *
 * @param usuarioId ID del usuario a mostrar
 * @param onNavigateToEdit Callback para navegar a la pantalla de edición
 * @param onCerrarSesion Callback para cerrar sesión
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuarioId: Long,
    onNavigateToEdit: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    usuarioViewModel: UsuarioViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val usuarioActual by loginViewModel.usuarioActual.collectAsState()

    var mostrarDialogoFoto by remember { mutableStateOf(false) }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    // Carga el usuario al iniciar
    LaunchedEffect(usuarioId) {
        usuarioViewModel.cargarUsuarioParaEdicion(usuarioId)
    }

    val estado by usuarioViewModel.estado.collectAsState()
    val usuario = estado.usuarioId?.let { estado.toUsuario() } ?: usuarioActual

    // Helper para cámara
    val cameraHelper = remember { CameraHelper(context) }

    // Launcher para tomar foto
    val tomarFotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            fotoUri?.let { uri ->
                usuario?.id?.let { id ->
                    usuarioViewModel.actualizarFoto(id, uri) {}
                }
            }
        }
    }

    // Launcher para galería
    val seleccionarGaleriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            usuario?.id?.let { id ->
                usuarioViewModel.actualizarFoto(id, it) {}
            }
        }
    }

    // Launcher para permisos
    val permisosCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fotoUri = cameraHelper.createImageUri()
            fotoUri?.let { tomarFotoLauncher.launch(it) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar perfil")
                    }
                    IconButton(onClick = { mostrarDialogoCerrarSesion = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        if (usuario == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto de perfil
                Box(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .clickable { mostrarDialogoFoto = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (usuario.tieneFotoPerfil()) {
                            Image(
                                painter = rememberAsyncImagePainter(usuario.fotoPerfil),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = usuario.getIniciales(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Badge de rol
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-8).dp, y = (-8).dp),
                        shape = CircleShape,
                        color = obtenerColorRol(usuario.rol)
                    ) {
                        Icon(
                            obtenerIconoRol(usuario.rol),
                            contentDescription = usuario.rol.displayName(),
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            tint = Color.White
                        )
                    }
                }

                // Nombre y username
                Text(
                    text = usuario.nombreCompleto,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "@${usuario.username}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Chip de rol
                Spacer(modifier = Modifier.height(8.dp))

                AssistChip(
                    onClick = { },
                    label = { Text(usuario.rol.displayName()) },
                    leadingIcon = {
                        Icon(
                            obtenerIconoRol(usuario.rol),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = obtenerColorRol(usuario.rol).copy(alpha = 0.2f),
                        labelColor = obtenerColorRol(usuario.rol)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Información detallada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Información Personal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        InfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = usuario.email
                        )

                        InfoRow(
                            icon = Icons.Default.Badge,
                            label = "RUT",
                            value = usuario.rut
                        )

                        usuario.telefono?.let {
                            InfoRow(
                                icon = Icons.Default.Phone,
                                label = "Teléfono",
                                value = it
                            )
                        }

                        InfoRow(
                            icon = if (usuario.activo) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            label = "Estado",
                            value = if (usuario.activo) "Activo" else "Inactivo",
                            valueColor = if (usuario.activo)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                OutlinedButton(
                    onClick = onNavigateToEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Perfil")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { mostrarDialogoFoto = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar Foto de Perfil")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { mostrarDialogoCerrarSesion = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            }
        }
    }

    // Diálogo para cambiar foto
    if (mostrarDialogoFoto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoFoto = false },
            title = { Text("Cambiar foto de perfil") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            mostrarDialogoFoto = false
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
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
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoFoto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para confirmar cierre de sesión
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        loginViewModel.cerrarSesion(onCerrarSesion)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCerrarSesion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Componente para mostrar una fila de información.
 */
@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Obtiene el color asociado a cada rol.
 */
@Composable
private fun obtenerColorRol(rol: Rol): Color {
    return when (rol) {
        Rol.PACIENTE -> Color(0xFF2196F3) // Azul
        Rol.MEDICO -> Color(0xFF4CAF50) // Verde
        Rol.ADMINISTRADOR -> Color(0xFFF44336) // Rojo
        Rol.RECEPCIONISTA -> Color(0xFFFF9800) // Naranja
    }
}

/**
 * Obtiene el icono asociado a cada rol.
 */
private fun obtenerIconoRol(rol: Rol): androidx.compose.ui.graphics.vector.ImageVector {
    return when (rol) {
        Rol.PACIENTE -> Icons.Default.Person
        Rol.MEDICO -> Icons.Default.MedicalServices
        Rol.ADMINISTRADOR -> Icons.Default.AdminPanelSettings
        Rol.RECEPCIONISTA -> Icons.Default.SupportAgent
    }
}
