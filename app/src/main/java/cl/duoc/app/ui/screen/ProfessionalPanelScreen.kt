package cl.duoc.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.data.entities.AppointmentEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalPanelScreen(
    viewModel: ProfessionalPanelViewModel
) {
    val citas by viewModel.citas.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mensaje) {
        mensaje?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.resetMensaje()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Panel del profesional", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            if (citas.isEmpty()) {
                Text("No tienes citas asignadas")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(citas) { cita ->
                        CitaItemProfesional(
                            cita = cita,
                            onAtendida = { viewModel.marcarComoAtendida(cita.id) },
                            onCancelada = { viewModel.cancelarCita(cita.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CitaItemProfesional(
    cita: AppointmentEntity,
    onAtendida: () -> Unit,
    onCancelada: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Paciente ID: ${cita.userId}")
            Text("Hora: ${cita.time}")
            Text("Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(cita.date))}")
            Text("Estado: ${cita.status}")

            if (cita.status == "Programada") {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onAtendida) {
                        Text("Atendida")
                    }

                    TextButton(onClick = onCancelada) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}