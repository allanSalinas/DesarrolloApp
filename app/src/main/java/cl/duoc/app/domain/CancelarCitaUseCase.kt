package cl.duoc.app.domain

import cl.duoc.app.model.data.repository.AppointmentRepository

class CancelarCitaUseCase(
    private val repository: AppointmentRepository
) {

    suspend operator fun invoke(appointmentId: Int): Result<Unit> {
        val cita = repository.obtenerCitaPorId(appointmentId)
            ?: return Result.failure(Exception("Cita no encontrada"))

        if (cita.status != "Programada") {
            return Result.failure(Exception("La cita no puede ser cancelada"))
        }

        // No permitir cancelar citas en el pasado (opcional pero recomendado)
        if (cita.date < System.currentTimeMillis()) {
            return Result.failure(Exception("No se puede cancelar una cita pasada"))
        }

        repository.cancelarCita(appointmentId)

        return Result.success(Unit)
    }
}