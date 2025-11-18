package cl.duoc.app.ui.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.data.entities.AppointmentEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyAppointmentsScreen(
    viewModel: MyAppointmentsViewModel
) {
    val appointments by viewModel.appointments.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarReservas()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Mis reservas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (appointments.isEmpty()) {
            Text("No tienes reservas agendadas.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(appointments) { item ->
                    AppointmentItem(item)
                }
            }
        }
    }
}

@Composable
private fun AppointmentItem(appointment: AppointmentEntity) {
    val sdf = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
    val fechaTexto = sdf.format(Date(appointment.date))

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Profesional: ${appointment.professional}")
            Text("Fecha: $fechaTexto a las ${appointment.time}")
            Text("Estado: ${appointment.status}")
        }
    }
}