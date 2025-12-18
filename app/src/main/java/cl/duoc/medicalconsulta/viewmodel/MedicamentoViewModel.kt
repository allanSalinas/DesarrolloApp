package cl.duoc.medicalconsulta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.medicalconsulta.model.data.repository.remote.MedicamentoRepository
import cl.duoc.medicalconsulta.model.domain.Medicamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MedicamentoUIState(
    val busqueda: String = "",
    val medicamentos: List<Medicamento> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val medicamentoSeleccionado: Medicamento? = null
)

class MedicamentoViewModel : ViewModel() {

    private val repository = MedicamentoRepository()

    private val _estado = MutableStateFlow(MedicamentoUIState())
    val estado: StateFlow<MedicamentoUIState> = _estado.asStateFlow()

    fun onBusquedaChange(nuevaBusqueda: String) {
        _estado.value = _estado.value.copy(busqueda = nuevaBusqueda)
    }

    fun buscarMedicamentos() {
        val query = _estado.value.busqueda.trim()

        if (query.isBlank()) {
            _estado.value = _estado.value.copy(error = "Ingrese un nombre de medicamento")
            return
        }

        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true, error = null)

            try {
                val medicamentos = repository.buscarMedicamentos(query)

                _estado.value = if (medicamentos.isEmpty()) {
                    _estado.value.copy(
                        medicamentos = emptyList(),
                        cargando = false,
                        error = "No se encontraron medicamentos con el nombre '$query'"
                    )
                } else {
                    _estado.value.copy(
                        medicamentos = medicamentos,
                        cargando = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false,
                    error = "Error al buscar medicamentos: ${e.message}"
                )
            }
        }
    }

    fun seleccionarMedicamento(medicamento: Medicamento?) {
        _estado.value = _estado.value.copy(medicamentoSeleccionado = medicamento)
    }

    fun limpiarBusqueda() {
        _estado.value = MedicamentoUIState()
    }
}
