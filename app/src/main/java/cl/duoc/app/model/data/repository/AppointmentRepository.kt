package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.dao.AppointmentDao
import cl.duoc.app.model.data.entities.AppointmentEntity

class AppointmentRepository(private val dao: AppointmentDao) {

    suspend fun agendarCita(appointment: AppointmentEntity) {
        dao.insertAppointment(appointment)
    }

    suspend fun obtenerCitasUsuarioActivas(userId: Int): List<AppointmentEntity> {
        return dao.getActiveAppointmentsByUser(userId)
    }

    suspend fun obtenerCitasUsuarioInactivas(userId: Int): List<AppointmentEntity> {
        return dao.getInactiveAppointmentsByUser(userId)
    }

    suspend fun obtenerCitaPorId(id: Int): AppointmentEntity? {
        return dao.getAppointmentById(id)
    }

    suspend fun actualizarCita(appointment: AppointmentEntity) {
        dao.updateAppointment(appointment)
    }

    suspend fun cancelarCita(id: Int) {
        dao.updateAppointmentStatus(id, "Cancelada")
    }

    // 👈 NUEVOS
    suspend fun obtenerCitasDelProfesional(professionalId: Int): List<AppointmentEntity> {
        return dao.getAppointmentsForProfessional(professionalId)
    }

    suspend fun actualizarEstadoCita(id: Int, status: String) {
        dao.updateAppointmentStatus(id, status)
    }
}