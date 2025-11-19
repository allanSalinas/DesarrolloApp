package cl.duoc.app.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.app.domain.*
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.data.repository.AppointmentRepository
import cl.duoc.app.model.data.repository.ProfessionalRepository
import cl.duoc.app.model.data.repository.SessionRepository
import cl.duoc.app.model.data.session.SessionManager
import cl.duoc.app.model.repository.UserRepository
import cl.duoc.app.ui.appointment.AppointmentScreen
import cl.duoc.app.ui.appointment.AppointmentViewModel
import cl.duoc.app.ui.appointment.MyAppointmentsScreen
import cl.duoc.app.ui.appointment.MyAppointmentsViewModel
import cl.duoc.app.ui.screen.*
import cl.duoc.app.viewmodel.RegisterViewModel

@Composable
fun AppNav(database: AppDatabase) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userId = sessionManager.getUserId() ?: 1 // ID de ejemplo si no hay sesión

    // --- REPOSITORIOS Y CASOS DE USO ---
    val userDao = database.userDao()
    val userRepository = UserRepository(userDao)
    val registerUseCase = RegisterUseCase(userRepository)

    val appointmentDao = database.appointmentDao()
    val appointmentRepository = AppointmentRepository(appointmentDao)
    val agendarCitaUseCase = AgendarCitaUseCase(appointmentRepository)
    val obtenerMisReservasUseCase = ObtenerMisReservasUseCase(appointmentRepository)
    val modificarCitaUseCase = ModificarCitaUseCase(appointmentRepository)
    val cancelarCitaUseCase = CancelarCitaUseCase(appointmentRepository)

    val professionalDao = database.professionalDao()
    val professionalRepository = ProfessionalRepository(professionalDao)
    val obtenerPerfilProfesionalUseCase = ObtenerPerfilProfesionalUseCase(professionalRepository)

    val sessionRepository = SessionRepository(sessionManager)
    val logoutUseCase = LogoutUseCase(sessionRepository)

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        // ... (otras rutas como START, LOGIN, REGISTER, HOME, AGENDAR_CITA, MIS_RESERVAS)

        composable(
            route = Routes.PERFIL_PROFESIONAL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            backStackEntry ->
            val professionalId = backStackEntry.arguments?.getInt("id") ?: 0
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProfessionalProfileViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return ProfessionalProfileViewModel(obtenerPerfilProfesionalUseCase, professionalId) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
            val professionalProfileViewModel: ProfessionalProfileViewModel = viewModel(factory = factory)

            ProfessionalProfileScreen(
                viewModel = professionalProfileViewModel,
                onAgendar = {
                    profId -> navController.navigate("${Routes.AGENDAR_CITA}/$profId")
                }
            )
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
    const val PERFIL_PROFESIONAL = "perfilProfesional/{id}" // 👈 RUTA NUEVA
}
