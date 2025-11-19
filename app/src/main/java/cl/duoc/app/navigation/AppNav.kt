package cl.duoc.app.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.app.domain.AgendarCitaUseCase
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.data.repository.AppointmentRepository
import cl.duoc.app.model.domain.RegisterUseCase
import cl.duoc.app.model.repository.UserRepository
import cl.duoc.app.ui.appointment.AppointmentScreen
import cl.duoc.app.ui.appointment.AppointmentViewModel
import cl.duoc.app.ui.screen.LoginScreen
import cl.duoc.app.ui.screen.RegisterScreen
import cl.duoc.app.ui.screen.StartScreen
import cl.duoc.app.viewmodel.RegisterViewModel

@Composable
fun AppNav(database: AppDatabase) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // --- CREACIÓN DE DEPENDENCIAS ---
    val userDao = database.userDao()
    val userRepository = UserRepository(userDao)
    val registerUseCase = RegisterUseCase(userRepository)

    val appointmentDao = database.appointmentDao()
    val appointmentRepository = AppointmentRepository(appointmentDao)
    val agendarCitaUseCase = AgendarCitaUseCase(appointmentRepository)

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartScreen(navController = navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(Routes.REGISTER) {
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
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

        composable(Routes.AGENDAR_CITA) {
            // Supongamos que el ID de usuario se obtiene de algún lugar (p.ej. un ViewModel de sesión)
            val userId = 1 // ID de usuario de ejemplo

            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return AppointmentViewModel(agendarCitaUseCase, userId) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }

            val appointmentViewModel: AppointmentViewModel = viewModel(factory = factory)

            AppointmentScreen(
                viewModel = appointmentViewModel,
                onSuccess = {
                    Toast.makeText(context, "Cita agendada con éxito", Toast.LENGTH_LONG).show()
                    navController.popBackStack() // Volver a la pantalla anterior
                }
            )
        }
    }
}

object Routes {
    const val START = "start"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val AGENDAR_CITA = "agendarCita"
    const val HOME_PACIENTE = "home_paciente"
    const val HOME_MEDICO = "home_medico"
}
