package cl.duoc.app.domain

import cl.duoc.app.model.data.entities.AppointmentEntity
import cl.duoc.app.model.data.repository.AppointmentRepository

class AgendarCitaUseCase(private val repository: AppointmentRepository) {

    suspend operator fun invoke(
        userId: Int,
        professional: String,
        date: Long,
        time: String
    ): Result<Unit> {

        // 🔥 Validación: no permitir fechas pasadas
        if (date < System.currentTimeMillis()) {
            return Result.failure(Exception("La fecha seleccionada no es válida"))
        }

        val appointment = AppointmentEntity(
            userId = userId,
            professional = professional,
            date = date,
            time = time
        )

        repository.agendarCita(appointment)
        return Result.success(Unit)
    }
}