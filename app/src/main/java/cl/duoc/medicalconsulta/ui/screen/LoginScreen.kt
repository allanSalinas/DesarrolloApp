package cl.duoc.medicalconsulta.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.medicalconsulta.ui.components.InputText
import cl.duoc.medicalconsulta.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onAuthenticated: (cl.duoc.medicalconsulta.model.domain.Usuario) -> Unit,
    onNavigateToRegistro: () -> Unit = {},
    onNavigateToRecuperar: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()
    var pwVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Medical Consulta",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Sistema de Gestión de Citas Médicas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            InputText(
                valor = estado.username,
                error = null,
                label = "Usuario",
                onChange = viewModel::onUsernameChange
            )

            OutlinedTextField(
                value = estado.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (pwVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default,
                trailingIcon = {
                    TextButton(onClick = { pwVisible = !pwVisible }) {
                        Text(if (pwVisible) "Ocultar" else "Mostrar")
                    }
                },
                isError = estado.error != null,
                supportingText = {
                    estado.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Enlace a recuperar contraseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNavigateToRecuperar) {
                    Text("¿Olvidaste tu contraseña?")
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.autenticar(onAuthenticated) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !estado.cargando
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Iniciar Sesión")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Enlace a registro
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onNavigateToRegistro) {
                    Text("Regístrate aquí")
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    LoginScreen(onAuthenticated = {})
}