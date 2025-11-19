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
import cl.duoc.medicalconsulta.ui.components.InputText
import cl.duoc.medicalconsulta.ui.components.ProfesionalCard
import cl.duoc.medicalconsulta.viewmodel.CitaViewModel
import cl.duoc.medicalconsulta.viewmodel.CitaViewModelFactory

@Composable
fun AgendarCitaScreen() {
    val context = LocalContext.current
    val viewModel: CitaViewModel = viewModel(
        factory = CitaViewModelFactory(context.applicationContext as Application)
    )

    val estado by viewModel.estado.collectAsState()
    val profesionales by viewModel.profesionales.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensaje de éxito
    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) {
            snackbarHostState.showSnackbar(
                message = "Cita agendada exitosamente",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Agendar Cita Médica",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Datos del Paciente
            item {
                Text(
                    text = "Datos del Paciente",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                InputText(
                    valor = estado.pacienteNombre,
                    error = estado.errores.pacienteNombre,
                    label = "Nombre completo",
                    onChange = viewModel::onNombreChange
                )
            }

            item {
                InputText(
                    valor = estado.pacienteRut,
                    error = estado.errores.pacienteRut,
                    label = "RUT (sin puntos, con guión)",
                    onChange = viewModel::onRutChange
                )
            }

            // Selección de Profesional
            item {
                Text(
                    text = "Seleccionar Profesional",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )

                estado.errores.profesional?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            items(profesionales) { profesional ->
                ProfesionalCard(
                    profesional = profesional,
                    seleccionado = estado.profesionalSeleccionado?.id == profesional.id,
                    onClick = { viewModel.onProfesionalSeleccionado(profesional) }
                )
            }

            // Fecha y Hora
            item {
                Text(
                    text = "Fecha y Hora",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InputText(
                        valor = estado.fecha,
                        error = estado.errores.fecha,
                        label = "Fecha (dd/mm/yyyy)",
                        onChange = viewModel::onFechaChange,
                        modifier = Modifier.weight(1f)
                    )

                    InputText(
                        valor = estado.hora,
                        error = estado.errores.hora,
                        label = "Hora (HH:MM)",
                        onChange = viewModel::onHoraChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Motivo de Consulta
            item {
                InputText(
                    valor = estado.motivoConsulta,
                    error = estado.errores.motivoConsulta,
                    label = "Motivo de consulta",
                    onChange = viewModel::onMotivoChange
                )
            }

            // Botón Agendar
            item {
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = viewModel::onAgendarCita,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !estado.errores.tieneErrores()
                ) {
                    Text("Agendar Cita")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AgendarCitaScreenPreview() {
    AgendarCitaScreen()
}