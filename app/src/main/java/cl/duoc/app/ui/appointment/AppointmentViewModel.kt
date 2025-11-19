package cl.duoc.app.ui.appointment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.AgendarCitaUseCase
import cl.duoc.app.domain.validation.FormValidator
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class AppointmentViewModel(
    private val agendarCitaUseCase: AgendarCitaUseCase,
    private val userId: Int
) : ViewModel() {

    var professional by mutableStateOf("")
    var dateStr by mutableStateOf("")
    var timeStr by mutableStateOf("")

    var professionalError by mutableStateOf<String?>(null)
    var dateError by mutableStateOf<String?>(null)
    var timeError by mutableStateOf<String?>(null)

    fun agendar(
        onSuccess: () -> Unit,
        onErrorGeneral: (String) -> Unit
    ) {
        // 1. Validar campos básicos
        val profValid = FormValidator.validateNotEmpty(professional, "Profesional")
        professionalError = profValid.errorMessage

        if (dateStr.trim().isEmpty()) {
            dateError = "Fecha obligatoria"
        } else {
            dateError = null
        }

        if (timeStr.trim().isEmpty()) {
            timeError = "Hora obligatoria"
        } else {
            timeError = null
        }

        if (!profValid.isValid || dateError != null || timeError != null) {
            onErrorGeneral("Por favor complete todos los campos obligatorios")
            return
        }

        // 2. Intentar parsear fecha/hora y validar que sea futura
        try {
            val localDate = LocalDate.parse(dateStr) // Espera formato "yyyy-MM-dd"
            val localTime = LocalTime.parse(timeStr) // Espera formato "HH:mm"

            val millis = localDate
                .atTime(localTime)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            if (millis < System.currentTimeMillis()) {
                dateError = "Seleccione una fecha/hora futura"
                onErrorGeneral("La fecha y hora no pueden ser en el pasado")
                return
            }

            // 3. Si todo válido, llamamos al caso de uso
            viewModelScope.launch {
                agendarCitaUseCase(userId, professional, millis, timeStr)
                    .onSuccess { onSuccess() }
                    .onFailure { e -> onErrorGeneral(e.message ?: "Error al agendar la cita") }
            }

        } catch (e: Exception) {
            dateError = "Formato de fecha (yyyy-MM-dd) u hora (HH:mm) inválido"
            onErrorGeneral("Formato de fecha u hora inválido")
        }
    }
}