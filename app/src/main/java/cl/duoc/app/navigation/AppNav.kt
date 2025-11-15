package cl.duoc.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.domain.RegisterUseCase // ¡CAMBIO! Ahora usamos el UseCase consolidado
import cl.duoc.app.model.repository.UserRepository
import cl.duoc.app.ui.screen.LoginScreen
import cl.duoc.app.ui.screen.RegisterScreen
import cl.duoc.app.ui.screen.StartScreen
import cl.duoc.app.viewmodel.RegisterViewModel

@Composable
fun AppNav(database: AppDatabase) {
    val navController = rememberNavController()

    // --- CREACIÓN DE DEPENDENCIAS ---
    val userDao = database.userDao()
    val userRepository = UserRepository(userDao)
    val registerUseCase = RegisterUseCase(userRepository) // ¡CAMBIO! Se instancia el UseCase correcto

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        // ...
        composable(Routes.REGISTER) {
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        // ¡CAMBIO! Se pasa el UseCase correcto a la fábrica del ViewModel
                        return RegisterViewModel(registerUseCase) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }

            val registerViewModel: RegisterViewModel = viewModel(factory = factory)

            RegisterScreen(
                navController = navController,
                viewModel = registerViewModel
            )
        }
        // ...
    }
}

// El objeto Routes y las NavigationActions permanecen igual...
object Routes {
    const val START = "start"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME_PACIENTE = "home_paciente"
    const val HOME_MEDICO = "home_medico"
    // ...etc
}
