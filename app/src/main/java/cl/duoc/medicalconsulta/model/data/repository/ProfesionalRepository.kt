package cl.duoc.medicalconsulta.model.data.repository

import cl.duoc.medicalconsulta.model.data.dao.ProfesionalDao
import cl.duoc.medicalconsulta.model.data.entities.ProfesionalEntity
import kotlinx.coroutines.flow.Flow

class ProfesionalRepository(private val dao: ProfesionalDao) {

    fun obtenerProfesionalesDisponibles(): Flow<List<ProfesionalEntity>> =
        dao.getProfesionalesDisponibles()

    fun obtenerTodosProfesionales(): Flow<List<ProfesionalEntity>> =
        dao.getAllProfesionales()

    suspend fun obtenerProfesionalPorId(id: Long): ProfesionalEntity? =
        dao.getProfesionalById(id)

    suspend fun guardarProfesional(profesional: ProfesionalEntity): Long =
        dao.insertProfesional(profesional)

    suspend fun limpiar() = dao.deleteAll()
}