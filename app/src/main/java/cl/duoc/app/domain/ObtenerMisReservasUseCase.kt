package cl.duoc.app.domain

import cl.duoc.app.model.data.entities.AppointmentEntity
import cl.duoc.app.model.data.repository.AppointmentRepository

class ObtenerMisReservasUseCase(
    private val repository: AppointmentRepository
) {

    suspend operator fun invoke(userId: Int): List<AppointmentEntity> {
        // Aquí podrías filtrar solo futuras si quieres, pero no es obligatorio
        return repository.obtenerCitasUsuario(userId)
    }
}