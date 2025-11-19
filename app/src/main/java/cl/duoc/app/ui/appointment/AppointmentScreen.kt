package cl.duoc.app.ui.appointment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppointmentScreen(
    viewModel: AppointmentViewModel,
    onSuccess: () -> Unit
) {
    var professional by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(Modifier.padding(20.dp)) {

        Text("Agendar Cita", fontSize = 24.sp)

        OutlinedTextField(
            value = professional,
            onValueChange = { professional = it },
            label = { Text("Profesional") }
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Fecha (yyyy-MM-dd)") }
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Hora (HH:mm)") }
        )

        Button(
            onClick = {
                viewModel.agendarCita(
                    professional,
                    date,
                    time,
                    onSuccess,
                    { error -> errorMessage = error }
                )
            }
        ) {
            Text("Agendar")
        }

        errorMessage?.let {
            Text(it, color = Color.Red)
        }
    }
}