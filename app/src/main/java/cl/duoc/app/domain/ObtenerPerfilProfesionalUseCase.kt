package cl.duoc.app.domain

import cl.duoc.app.model.data.entities.ProfessionalEntity
import cl.duoc.app.model.data.repository.ProfessionalRepository

class ObtenerPerfilProfesionalUseCase(
    private val repository: ProfessionalRepository
) {

    suspend operator fun invoke(id: Int): Result<ProfessionalEntity> {
        val profesional = repository.obtenerProfesional(id)
            ?: return Result.failure(Exception("Profesional no encontrado"))

        return Result.success(profesional)
    }
}