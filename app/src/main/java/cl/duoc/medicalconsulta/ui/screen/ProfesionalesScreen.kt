package cl.duoc.medicalconsulta.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.medicalconsulta.model.domain.Profesional
import cl.duoc.medicalconsulta.ui.components.ProfesionalCard
import cl.duoc.medicalconsulta.viewmodel.ProfesionalesViewModel
import cl.duoc.medicalconsulta.viewmodel.ProfesionalesViewModelFactory

@Composable
fun ProfesionalesScreen() {
    val context = LocalContext.current
    val viewModel: ProfesionalesViewModel = viewModel(
        factory = ProfesionalesViewModelFactory(context.applicationContext as Application)
    )

    val profesionales by viewModel.profesionales.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profesionales Disponibles",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${profesionales.size} profesionales disponibles",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        if (profesionales.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "No hay profesionales disponibles",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(profesionales) { profesional ->
                    ProfesionalCard(profesional = profesional)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfesionalesScreenPreview() {
    ProfesionalesScreen()
}