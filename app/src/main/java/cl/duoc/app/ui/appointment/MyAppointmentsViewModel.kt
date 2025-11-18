package cl.duoc.app.ui.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.CancelarCitaUseCase
import cl.duoc.app.domain.ModificarCitaUseCase
import cl.duoc.app.domain.ObtenerMisReservasUseCase
import cl.duoc.app.model.data.entities.AppointmentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MyAppointmentsViewModel(
    private val obtenerMisReservasUseCase: ObtenerMisReservasUseCase,
    private val modificarCitaUseCase: ModificarCitaUseCase,
    private val cancelarCitaUseCase: CancelarCitaUseCase,
    private val userId: Int
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<AppointmentEntity>>(emptyList())
    val appointments: StateFlow<List<AppointmentEntity>> = _appointments

    fun cargarReservas() {
        viewModelScope.launch {
            // Llama al caso de uso que ahora solo trae citas activas
            _appointments.value = obtenerMisReservasUseCase(userId)
        }
    }

    fun cancelarCita(
        appointmentId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            cancelarCitaUseCase(appointmentId)
                .onSuccess {
                    cargarReservas() // Recarga la lista, que ahora excluirá la cita cancelada
                    onSuccess()
                }
                .onFailure { onError(it.message ?: "Error al cancelar la cita") }
        }
    }

    fun modificarCita(
        appointmentId: Int,
        newProfessional: String,
        newDate: LocalDate,
        newTime: LocalTime,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            modificarCitaUseCase(appointmentId, newProfessional, newDate, newTime)
                .onSuccess {
                    cargarReservas() // Recarga la lista para mostrar los datos actualizados
                    onSuccess()
                }
                .onFailure { onError(it.message ?: "Error al modificar la cita") }
        }
    }
}