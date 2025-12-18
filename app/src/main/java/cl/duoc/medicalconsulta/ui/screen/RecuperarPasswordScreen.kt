package cl.duoc.medicalconsulta.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.medicalconsulta.viewmodel.UsuarioViewModel

/**
 * Pantalla para recuperación de contraseña.
 * Incluye dos pasos:
 * 1. Solicitar recuperación (ingresa email)
 * 2. Restablecer contraseña (ingresa token y nueva contraseña)
 *
 * @param onRecuperacionExitosa Callback ejecutado cuando se completa el proceso
 * @param onNavigateBack Callback para navegar hacia atrás
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarPasswordScreen(
    onRecuperacionExitosa: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: UsuarioViewModel = viewModel()
) {
    val estado by viewModel.estadoRecuperacion.collectAsState()

    // Efectos para mensajes de éxito
    LaunchedEffect(estado.mensajeExito) {
        estado.mensajeExito?.let {
            if (estado.paso == 2 && it.contains("éxito", ignoreCase = true)) {
                // Después de restablecer exitosamente, navega al login
                kotlinx.coroutines.delay(2000)
                onRecuperacionExitosa()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (estado.paso == 1) "Recuperar Contraseña"
                        else "Restablecer Contraseña"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (estado.paso) {
                1 -> PasoSolicitarRecuperacion(
                    estado = estado,
                    onEmailChange = { viewModel.onEmailRecuperacionChange(it) },
                    onSolicitar = { viewModel.solicitarRecuperacion {} }
                )
                2 -> PasoRestablecerPassword(
                    estado = estado,
                    onTokenChange = { viewModel.onTokenChange(it) },
                    onNuevaPasswordChange = { viewModel.onNuevaPasswordChange(it) },
                    onConfirmarPasswordChange = { viewModel.onConfirmarNuevaPasswordChange(it) },
                    onRestablecer = { viewModel.restablecerPassword(onRecuperacionExitosa) }
                )
            }
        }
    }
}

/**
 * Paso 1: Solicitar recuperación de contraseña.
 * El usuario ingresa su email y se le envía un token.
 */
@Composable
private fun PasoSolicitarRecuperacion(
    estado: cl.duoc.medicalconsulta.model.domain.RecuperacionPasswordUIState,
    onEmailChange: (String) -> Unit,
    onSolicitar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.LockReset,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ingresa tu correo electrónico y te enviaremos un código para restablecer tu contraseña.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Email
            OutlinedTextField(
                value = estado.email,
                onValueChange = onEmailChange,
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = estado.emailError != null,
                supportingText = {
                    estado.emailError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            // Mensaje de error
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
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Mensaje de éxito (token enviado)
            estado.mensajeExito?.let { exito ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = exito,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Revisa tu correo y usa el código en el siguiente paso.",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de solicitar
            Button(
                onClick = onSolicitar,
                enabled = !estado.cargando && estado.paso1Valido(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar código de recuperación")
                }
            }
        }
    }
}

/**
 * Paso 2: Restablecer contraseña con el token recibido.
 */
@Composable
private fun PasoRestablecerPassword(
    estado: cl.duoc.medicalconsulta.model.domain.RecuperacionPasswordUIState,
    onTokenChange: (String) -> Unit,
    onNuevaPasswordChange: (String) -> Unit,
    onConfirmarPasswordChange: (String) -> Unit,
    onRestablecer: () -> Unit
) {
    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmarPassword by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.VpnKey,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Restablecer Contraseña",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ingresa el código que recibiste por email y tu nueva contraseña.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Token
            OutlinedTextField(
                value = estado.token,
                onValueChange = onTokenChange,
                label = { Text("Código de recuperación") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                isError = estado.tokenError != null,
                supportingText = {
                    estado.tokenError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            // Campo: Nueva Contraseña
            OutlinedTextField(
                value = estado.nuevaPassword,
                onValueChange = onNuevaPasswordChange,
                label = { Text("Nueva contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                        Icon(
                            if (mostrarPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (mostrarPassword) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                isError = estado.nuevaPasswordError != null,
                supportingText = {
                    estado.nuevaPasswordError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            // Campo: Confirmar Nueva Contraseña
            OutlinedTextField(
                value = estado.confirmarPassword,
                onValueChange = onConfirmarPasswordChange,
                label = { Text("Confirmar nueva contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { mostrarConfirmarPassword = !mostrarConfirmarPassword }) {
                        Icon(
                            if (mostrarConfirmarPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (mostrarConfirmarPassword) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (mostrarConfirmarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                isError = estado.confirmarPasswordError != null,
                supportingText = {
                    estado.confirmarPasswordError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            // Mensaje de error
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
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Mensaje de éxito
            estado.mensajeExito?.let { exito ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = exito,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de restablecer
            Button(
                onClick = onRestablecer,
                enabled = !estado.cargando && estado.paso2Valido(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restablecer contraseña")
                }
            }
        }
    }
}
