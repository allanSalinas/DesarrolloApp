package cl.duoc.app.domain

import cl.duoc.app.model.data.repository.AppointmentRepository

class ActualizarEstadoCitaUseCase(
    private val repository: AppointmentRepository
) {

    suspend operator fun invoke(id: Int, status: String): Result<Unit> {

        if (status !in listOf("Atendida", "Cancelada")) {
            return Result.failure(Exception("Estado inválido"))
        }

        repository.actualizarEstadoCita(id, status)

        return Result.success(Unit)
    }
}