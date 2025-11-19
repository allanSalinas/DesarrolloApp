package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.dao.AppointmentDao
import cl.duoc.app.model.data.entities.AppointmentEntity

class AppointmentRepository(private val dao: AppointmentDao) {

    suspend fun agendarCita(appointment: AppointmentEntity) {
        dao.insertAppointment(appointment)
    }

    suspend fun obtenerCitasUsuario(userId: Int): List<AppointmentEntity> {
        return dao.getAppointmentsByUser(userId)
    }
}