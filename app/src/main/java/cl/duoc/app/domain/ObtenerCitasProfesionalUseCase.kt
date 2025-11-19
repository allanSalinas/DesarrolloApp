package cl.duoc.app.domain

import cl.duoc.app.model.data.entities.AppointmentEntity
import cl.duoc.app.model.data.repository.AppointmentRepository

class ObtenerCitasProfesionalUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(professionalId: Int): List<AppointmentEntity> {
        return repository.obtenerCitasDelProfesional(professionalId)
    }
}