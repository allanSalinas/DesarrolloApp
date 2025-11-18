package cl.duoc.app.ui.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.AgendarCitaUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class AppointmentViewModel(
    private val useCase: AgendarCitaUseCase,
    private val userId: Int
) : ViewModel() {

    fun agendarCita(
        professional: String,
        dateStr: String,
        time: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val dateMillis = LocalDate.parse(dateStr)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                useCase(userId, professional, dateMillis, time)
                    .onSuccess { onSuccess() }
                    .onFailure { onError(it.message ?: "Error") }

            } catch (e: Exception) {
                onError("Fecha inválida")
            }
        }
    }
}