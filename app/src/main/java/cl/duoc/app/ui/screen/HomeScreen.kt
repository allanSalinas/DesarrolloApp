package cl.duoc.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cl.duoc.app.domain.LogoutUseCase

@Composable
fun HomeScreen(
    navController: NavHostController,
    logoutUseCase: LogoutUseCase
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Aquí podrías agregar más contenido para tu pantalla de inicio
        Text("Bienvenido a la pantalla de inicio")

        Button(
            onClick = {
                val result = logoutUseCase()
                if (result.isSuccess) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        ) {
            Text("Cerrar sesión")
        }
    }
}