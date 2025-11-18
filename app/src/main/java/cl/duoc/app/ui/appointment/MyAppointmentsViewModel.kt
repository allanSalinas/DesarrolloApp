package cl.duoc.app.ui.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.ObtenerMisReservasUseCase
import cl.duoc.app.model.data.entities.AppointmentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyAppointmentsViewModel(
    private val useCase: ObtenerMisReservasUseCase,
    private val userId: Int
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<AppointmentEntity>>(emptyList())
    val appointments: StateFlow<List<AppointmentEntity>> = _appointments

    fun cargarReservas() {
        viewModelScope.launch {
            val result = useCase(userId)
            _appointments.value = result
        }
    }
}