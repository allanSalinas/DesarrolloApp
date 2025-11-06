package cl.duoc.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.app.model.data.AppDatabase
import cl.duoc.app.model.repository.UserRepository
import cl.duoc.app.ui.components.InputText
import cl.duoc.app.viewmodel.RegisterViewModel
import cl.duoc.app.viewmodel.RegisterViewModelFactory

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(db.userDao())
    val factory = RegisterViewModelFactory(userRepository)
    val viewModel: RegisterViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.registrationSuccess) {
        Toast.makeText(context, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
        navController.popBackStack()
    }

    uiState.error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputText(label = "Nombre", value = uiState.name, onValueChange = { viewModel.onNameChange(it) })
        Spacer(modifier = Modifier.height(8.dp))
        InputText(label = "Correo", value = uiState.email, onValueChange = { viewModel.onEmailChange(it) })
        Spacer(modifier = Modifier.height(8.dp))
        InputText(label = "Contraseña", value = uiState.pass, onValueChange = { viewModel.onPasswordChange(it) }, isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.register() }) {
            Text("Registrar")
        }
    }
}
