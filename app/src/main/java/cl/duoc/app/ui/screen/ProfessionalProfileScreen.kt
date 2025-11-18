package cl.duoc.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfessionalProfileScreen(
    viewModel: ProfessionalProfileViewModel,
    onAgendar: (Int) -> Unit
) {
    val profesional by viewModel.profesional.collectAsState()
    val error by viewModel.error.collectAsState()

    if (error != null) {
        Text("Error: $error")
        return
    }

    profesional?.let { prof ->
        Column(Modifier.padding(16.dp)) {

            Text(prof.name, style = MaterialTheme.typography.titleLarge)
            Text("Especialidad: ${prof.specialty}")
            Spacer(Modifier.height(8.dp))
            Text(prof.description)
            Spacer(Modifier.height(12.dp))

            val horarios = prof.availableHours.split(",").filter { it.isNotBlank() }

            Text("Horarios disponibles:", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (horarios.isEmpty()) {
                Text("No hay horarios disponibles")
            } else {
                horarios.forEach { hora ->
                    Text("• $hora")
                }

                Spacer(Modifier.height(16.dp))
                Button(onClick = { onAgendar(prof.id) }) {
                    Text("Agendar cita")
                }
            }
        }
    }
}