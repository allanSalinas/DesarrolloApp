package cl.duoc.app.domain

import cl.duoc.app.model.data.repository.SessionRepository

class LogoutUseCase(
    private val sessionRepository: SessionRepository
) {

    operator fun invoke(): Result<Unit> {
        return try {
            sessionRepository.cerrarSesion()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}