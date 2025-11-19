package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.dao.ProfessionalDao

class ProfessionalRepository(private val dao: ProfessionalDao) {

    suspend fun obtenerProfesional(id: Int) =
        dao.getProfessionalById(id)

    suspend fun obtenerProfesionales() =
        dao.getAllProfessionals()
}