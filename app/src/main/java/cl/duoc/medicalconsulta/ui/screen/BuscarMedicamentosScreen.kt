package cl.duoc.medicalconsulta.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.medicalconsulta.model.domain.Medicamento
import cl.duoc.medicalconsulta.viewmodel.MedicamentoViewModel

@Composable
fun BuscarMedicamentosScreen(
    viewModel: MedicamentoViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Búsqueda de Medicamentos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Información proporcionada por OpenFDA (FDA - USA)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo de búsqueda
        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = { viewModel.onBusquedaChange(it) },
            label = { Text("Nombre del medicamento") },
            placeholder = { Text("Ej: aspirin, ibuprofen, amoxicillin, ") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            trailingIcon = {
                if (estado.busqueda.isNotEmpty()) {
                    IconButton(onClick = { viewModel.limpiarBusqueda() }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de búsqueda
        Button(
            onClick = { viewModel.buscarMedicamentos() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estado.cargando && estado.busqueda.isNotBlank()
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (estado.cargando) "Buscando..." else "Buscar Medicamento")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error
        estado.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lista de resultados
        if (estado.medicamentos.isNotEmpty()) {
            Text(
                text = "Resultados (${estado.medicamentos.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.medicamentos) { medicamento ->
                    MedicamentoCard(
                        medicamento = medicamento,
                        onClick = { viewModel.seleccionarMedicamento(medicamento) }
                    )
                }
            }
        }
    }

    // Dialog de detalle
    estado.medicamentoSeleccionado?.let { medicamento ->
        MedicamentoDetalleDialog(
            medicamento = medicamento,
            onDismiss = { viewModel.seleccionarMedicamento(null) }
        )
    }
}

@Composable
fun MedicamentoCard(
    medicamento: Medicamento,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = medicamento.nombreComercial,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Genérico: ${medicamento.nombreGenerico}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Fabricante: ${medicamento.fabricante}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (medicamento.proposito != "No disponible") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Propósito: ${medicamento.proposito.take(100)}...",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun MedicamentoDetalleDialog(
    medicamento: Medicamento,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(medicamento.nombreComercial)
        },
        text = {
            LazyColumn {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetalleItem("Nombre Genérico", medicamento.nombreGenerico)
                        DetalleItem("Fabricante", medicamento.fabricante)
                        DetalleItem("Ingrediente Activo", medicamento.ingredienteActivo)
                        DetalleItem("Vía de Administración", medicamento.viaAdministracion)
                        DetalleItem("Propósito", medicamento.proposito)
                        DetalleItem("Indicaciones", medicamento.indicaciones)
                        DetalleItem("Advertencias", medicamento.advertencias)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun DetalleItem(label: String, value: String) {
    if (value != "No disponible") {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
