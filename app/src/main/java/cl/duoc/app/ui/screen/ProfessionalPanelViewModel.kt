package cl.duoc.app.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.ActualizarEstadoCitaUseCase
import cl.duoc.app.domain.ObtenerCitasProfesionalUseCase
import cl.duoc.app.model.data.entities.AppointmentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfessionalPanelViewModel(
    private val obtenerCitasUseCase: ObtenerCitasProfesionalUseCase,
    private val actualizarEstadoCitaUseCase: ActualizarEstadoCitaUseCase,
    private val professionalId: Int
) : ViewModel() {

    private val _citas = MutableStateFlow<List<AppointmentEntity>>(emptyList())
    val citas: StateFlow<List<AppointmentEntity>> = _citas

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    init {
        cargarCitas()
    }

    fun cargarCitas() {
        viewModelScope.launch {
            _citas.value = obtenerCitasUseCase(professionalId)
        }
    }

    fun marcarComoAtendida(id: Int) {
        viewModelScope.launch {
            actualizarEstadoCitaUseCase(id, "Atendida")
                .onSuccess {
                    _mensaje.value = "Cita marcada como atendida"
                    cargarCitas()
                }
        }
    }

    fun cancelarCita(id: Int) {
        viewModelScope.launch {
            actualizarEstadoCitaUseCase(id, "Cancelada")
                .onSuccess {
                    _mensaje.value = "Cita cancelada correctamente"
                    cargarCitas()
                }
        }
    }

    fun resetMensaje() {
        _mensaje.value = null
    }
}