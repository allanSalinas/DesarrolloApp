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
import cl.duoc.app.domain.CancelarCitaUseCase
import cl.duoc.app.domain.LogoutUseCase
import cl.duoc.app.domain.ModificarCitaUseCase
import cl.duoc.app.domain.ObtenerMisReservasUseCase
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.data.repository.AppointmentRepository
import cl.duoc.app.model.data.repository.SessionRepository
import cl.duoc.app.model.data.session.SessionManager
import cl.duoc.app.model.domain.RegisterUseCase
import cl.duoc.app.model.repository.UserRepository
import cl.duoc.app.ui.appointment.AppointmentScreen
import cl.duoc.app.ui.appointment.AppointmentViewModel
import cl.duoc.app.ui.appointment.MyAppointmentsScreen
import cl.duoc.app.ui.appointment.MyAppointmentsViewModel
import cl.duoc.app.ui.screen.HomeScreen
import cl.duoc.app.ui.screen.LoginScreen
import cl.duoc.app.ui.screen.RegisterScreen
import cl.duoc.app.ui.screen.StartScreen
import cl.duoc.app.viewmodel.RegisterViewModel

@Composable
fun AppNav(database: AppDatabase) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userId = sessionManager.getUserId() ?: 1 // ID de ejemplo si no hay sesión

    // --- CREACIÓN DE DEPENDENCIAS ---
    val userDao = database.userDao()
    val userRepository = UserRepository(userDao)
    val registerUseCase = RegisterUseCase(userRepository)

    val appointmentDao = database.appointmentDao()
    val appointmentRepository = AppointmentRepository(appointmentDao)
    val agendarCitaUseCase = AgendarCitaUseCase(appointmentRepository)
    val obtenerMisReservasUseCase = ObtenerMisReservasUseCase(appointmentRepository)
    val modificarCitaUseCase = ModificarCitaUseCase(appointmentRepository) // 👈 NUEVO
    val cancelarCitaUseCase = CancelarCitaUseCase(appointmentRepository)   // 👈 NUEVO

    val sessionRepository = SessionRepository(sessionManager)
    val logoutUseCase = LogoutUseCase(sessionRepository)

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        // ... (otras rutas como START, LOGIN, REGISTER, HOME, AGENDAR_CITA)

        composable(Routes.MIS_RESERVAS) {
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MyAppointmentsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        // AHORA PASAMOS TODOS LOS CASOS DE USO NECESARIOS
                        return MyAppointmentsViewModel(
                            obtenerMisReservasUseCase,
                            modificarCitaUseCase,
                            cancelarCitaUseCase,
                            userId
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
            val myAppointmentsViewModel: MyAppointmentsViewModel = viewModel(factory = factory)
            MyAppointmentsScreen(viewModel = myAppointmentsViewModel)
        }
    }
}

object Routes {
    const val START = "start"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val AGENDAR_CITA = "agendarCita"
    const val MIS_RESERVAS = "misReservas"
}
