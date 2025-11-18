package cl.duoc.app.ui.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.data.entities.AppointmentEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(
    viewModel: MyAppointmentsViewModel
) {
    val appointments by viewModel.appointments.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.cargarReservas()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Mis reservas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (appointments.isEmpty()) {
                Text("No tienes reservas agendadas.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(appointments) { item ->
                        AppointmentItem(
                            appointment = item,
                            onModificar = {
                                // Aquí se navegaría a la pantalla de modificación
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Navegando a modificar cita...")
                                }
                            },
                            onCancelarConfirmado = {
                                viewModel.cancelarCita(
                                    appointmentId = item.id,
                                    onSuccess = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("✅ Cita cancelada correctamente")
                                        }
                                    },
                                    onError = { msg ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(msg)
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentItem(
    appointment: AppointmentEntity,
    onModificar: () -> Unit,
    onCancelarConfirmado: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
    val fechaTexto = sdf.format(Date(appointment.date))
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cancelar cita") },
            text = { Text("¿Estás seguro de que deseas cancelar esta cita?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    onCancelarConfirmado()
                }) {
                    Text("Sí, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("No")
                }
            }
        )
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Profesional: ${appointment.professional}")
            Text("Fecha: $fechaTexto a las ${appointment.time}")
            Text("Estado: ${appointment.status}")

            // Solo mostrar botones si la cita está programada
            if (appointment.status == "Programada") {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    TextButton(onClick = onModificar) {
                        Text("Modificar")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { mostrarDialogo = true }) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}