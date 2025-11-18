package cl.duoc.app.domain

import cl.duoc.app.model.data.entities.AppointmentEntity
import cl.duoc.app.model.data.repository.AppointmentRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class ModificarCitaUseCase(
    private val repository: AppointmentRepository
) {

    suspend operator fun invoke(
        appointmentId: Int,
        newProfessional: String,
        newDate: LocalDate,
        newTime: LocalTime
    ): Result<Unit> {
        val cita = repository.obtenerCitaPorId(appointmentId)
            ?: return Result.failure(Exception("Cita no encontrada"))

        // Solo se puede modificar si está Programada
        if (cita.status != "Programada") {
            return Result.failure(Exception("La cita no puede ser modificada"))
        }

        // Nueva fecha/hora en millis
        val newDateMillis = newDate
            .atTime(newTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // No permitir fechas pasadas
        if (newDateMillis < System.currentTimeMillis()) {
            return Result.failure(Exception("La fecha/hora seleccionada no es válida"))
        }

        val citaActualizada = cita.copy(
            professional = newProfessional,
            date = newDateMillis,
            time = newTime.toString()
        )

        repository.actualizarCita(citaActualizada)

        return Result.success(Unit)
    }
}