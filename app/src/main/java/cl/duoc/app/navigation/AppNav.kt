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
    val userId = sessionManager.getUserId() ?: 1 
    // Para un profesional, obtendríamos su ID específico. Usaremos un ejemplo.
    val professionalId = 1 // ID de profesional de ejemplo

    // --- REPOSITORIOS Y CASOS DE USO ---
    val appointmentDao = database.appointmentDao()
    val appointmentRepository = AppointmentRepository(appointmentDao)
    val obtenerMisReservasUseCase = ObtenerMisReservasUseCase(appointmentRepository)
    val modificarCitaUseCase = ModificarCitaUseCase(appointmentRepository)
    val cancelarCitaUseCase = CancelarCitaUseCase(appointmentRepository)
    val agendarCitaUseCase = AgendarCitaUseCase(appointmentRepository)

    val professionalDao = database.professionalDao()
    val professionalRepository = ProfessionalRepository(professionalDao)
    val obtenerPerfilProfesionalUseCase = ObtenerPerfilProfesionalUseCase(professionalRepository)

    val sessionRepository = SessionRepository(sessionManager)
    val logoutUseCase = LogoutUseCase(sessionRepository)

    val userDao = database.userDao()
    val userRepository = UserRepository(userDao)
    val registerUseCase = RegisterUseCase(userRepository)
    
    // Casos de uso para el panel del profesional (HU-10)
    val obtenerCitasProfesionalUseCase = ObtenerCitasProfesionalUseCase(appointmentRepository)
    val actualizarEstadoCitaUseCase = ActualizarEstadoCitaUseCase(appointmentRepository)

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        // ... (Otras rutas)

        composable(Routes.PANEL_PROFESIONAL) { // 👈 RUTA NUEVA
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProfessionalPanelViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return ProfessionalPanelViewModel(
                            obtenerCitasUseCase = obtenerCitasProfesionalUseCase,
                            actualizarEstadoCitaUseCase = actualizarEstadoCitaUseCase,
                            professionalId = professionalId
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
            val professionalPanelViewModel: ProfessionalPanelViewModel = viewModel(factory = factory)
            ProfessionalPanelScreen(viewModel = professionalPanelViewModel)
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
    const val PERFIL_PROFESIONAL = "perfilProfesional/{id}"
    const val PANEL_PROFESIONAL = "panelProfesional" // 👈 RUTA NUEVA
}
