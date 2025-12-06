package cl.duoc.medicalconsulta.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import cl.duoc.medicalconsulta.ui.screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Routes {
    const val Login = "login"
    const val Start = "start"
    const val Profesionales = "profesionales"
    const val AgendarCita = "agendar_cita"
    const val Historial = "historial"
    const val BuscarMedicamentos = "buscar_medicamentos"
}

const val CITA_ID_KEY = "citaId"
const val RUTA_AGENDAR_EDITAR = "${Routes.AgendarCita}?$CITA_ID_KEY={$CITA_ID_KEY}"

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavHost(navController = nav, startDestination = Routes.Login) {
        // LOGIN
        composable(Routes.Login) {
            LoginScreen(
                onAuthenticated = {
                    nav.navigate(Routes.Start) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // SHELL (drawer + scaffold)
        navigation(startDestination = Routes.Start, route = "main_shell") {
            composable(Routes.Start) {
                DrawerScaffold(
                    currentRoute = Routes.Start,
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope
                ) {
                    StartScreen()
                }
            }

            composable(Routes.Profesionales) {
                DrawerScaffold(
                    currentRoute = Routes.Profesionales,
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope
                ) {
                    ProfesionalesScreen()
                }
            }

            // Al usar defaultValue, esta ruta maneja tanto "agendar_cita" como "agendar_cita?citaId=X"
            composable(
                route = RUTA_AGENDAR_EDITAR,
                arguments = listOf(navArgument(CITA_ID_KEY) {
                    defaultValue = 0L // 0L indica que no hay ID de edición
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val citaId = backStackEntry.arguments?.getLong(CITA_ID_KEY) ?: 0L
                DrawerScaffold(
                    currentRoute = Routes.AgendarCita,
                    onNavigate = { route -> 
                        // Si navegan a Agendar Cita desde el menú, limpiamos el backstack o vamos a la ruta base
                        nav.navigate(route) {
                            launchSingleTop = true
                        }
                    },
                    drawerState = drawerState,
                    scope = scope
                ) {
                    // Pasamos el ID a la pantalla
                    AgendarCitaScreen(citaId = citaId)
                }
            }

            composable(Routes.Historial) {
                DrawerScaffold(
                    currentRoute = Routes.Historial,
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope
                ) {
                    HistorialCitasScreen(
                        onNavigateToEdit = { id ->
                            nav.navigate("${Routes.AgendarCita}?$CITA_ID_KEY=$id")
                        }
                    )
                }
            }

            composable(Routes.BuscarMedicamentos) {
                DrawerScaffold(
                    currentRoute = Routes.BuscarMedicamentos,
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope
                ) {
                    BuscarMedicamentosScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    content: @Composable () -> Unit
) {
    val destinations = listOf(
        DrawerItem("Inicio", Routes.Start),
        DrawerItem("Profesionales", Routes.Profesionales),
        DrawerItem("Agendar Cita", Routes.AgendarCita),
        DrawerItem("Historial de Citas", Routes.Historial),
        DrawerItem("Buscar Medicamentos", Routes.BuscarMedicamentos)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Medical Consulta",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                destinations.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != item.route) {
                                onNavigate(item.route)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(appBarTitle(currentRoute)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { padding ->
            Surface(Modifier.padding(padding)) {
                content()
            }
        }
    }
}

private data class DrawerItem(val label: String, val route: String)

@Composable
private fun appBarTitle(route: String?): String = when (route) {
    Routes.Start -> "Inicio"
    Routes.Profesionales -> "Profesionales Disponibles"
    Routes.AgendarCita -> "Agendar Cita Médica"
    Routes.Historial -> "Historial de Citas"
    Routes.BuscarMedicamentos -> "Buscar Medicamentos"
    else -> "Medical Consulta"
}
