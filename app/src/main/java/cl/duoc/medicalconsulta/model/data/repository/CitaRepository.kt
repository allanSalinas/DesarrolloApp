package cl.duoc.medicalconsulta.model.data.repository

import cl.duoc.medicalconsulta.model.data.dao.CitaDao
import cl.duoc.medicalconsulta.model.data.entities.CitaEntity
import kotlinx.coroutines.flow.Flow

class CitaRepository(private val dao: CitaDao) {

    fun obtenerTodasCitas(): Flow<List<CitaEntity>> =
        dao.getAllCitas()

    suspend fun obtenerCitaPorId(id: Long): CitaEntity? =
        dao.getCitaById(id)

    suspend fun guardarCita(cita: CitaEntity): Long =
        dao.insertCita(cita)

    suspend fun eliminarCita(id: Long) =
        dao.deleteCita(id)

    suspend fun limpiar() = dao.deleteAll()
}