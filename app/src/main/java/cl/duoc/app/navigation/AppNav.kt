package cl.duoc.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.app.ui.screen.LoginScreen
import cl.duoc.app.ui.screen.RegisterScreen
import cl.duoc.app.ui.screen.StartScreen

/**
 * Sistema de navegación principal de la aplicación
 * Gestiona las rutas y el flujo de navegación entre pantallas
 */
@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        // Pantalla de inicio
        composable(Routes.START) {
            StartScreen(navController = navController)
        }

        // Pantalla de login
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // Pantalla de registro
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // Pantalla principal de paciente
        composable(Routes.HOME_PACIENTE) {
            // TODO: Implementar HomeScreen para pacientes
            // HomePacienteScreen(navController = navController)
        }

        // Pantalla principal de médico
        composable(Routes.HOME_MEDICO) {
            // TODO: Implementar HomeScreen para médicos
            // HomeMedicoScreen(navController = navController)
        }

        // Perfil de usuario
        composable(
            route = "${Routes.PERFIL}/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            // TODO: Implementar PerfilScreen
            // PerfilScreen(navController = navController, userId = userId)
        }
    }
}

/**
 * Objeto que contiene todas las rutas de navegación de la aplicación
 */
object Routes {
    const val START = "start"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME_PACIENTE = "home_paciente"
    const val HOME_MEDICO = "home_medico"
    const val PERFIL = "perfil"
    const val MEDICOS_LIST = "medicos_list"
    const val MEDICO_DETAIL = "medico_detail"
    const val AGENDAR_CITA = "agendar_cita"
    const val MIS_CITAS = "mis_citas"
}

/**
 * Funciones de extensión para facilitar la navegación
 */
object NavigationActions {
    fun navigateToLogin(navController: androidx.navigation.NavController) {
        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.START) { inclusive = true }
        }
    }

    fun navigateToRegister(navController: androidx.navigation.NavController) {
        navController.navigate(Routes.REGISTER)
    }

    fun navigateToHome(
        navController: androidx.navigation.NavController,
        tipoUsuario: String
    ) {
        val route = if (tipoUsuario == "MEDICO") {
            Routes.HOME_MEDICO
        } else {
            Routes.HOME_PACIENTE
        }

        navController.navigate(route) {
            popUpTo(Routes.LOGIN) { inclusive = true }
        }
    }

    fun navigateToPerfil(
        navController: androidx.navigation.NavController,
        userId: Int
    ) {
        navController.navigate("${Routes.PERFIL}/$userId")
    }
}