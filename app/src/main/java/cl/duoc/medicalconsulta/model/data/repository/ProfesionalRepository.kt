package cl.duoc.medicalconsulta.model.data.repository

import android.util.Log
import cl.duoc.medicalconsulta.model.data.dao.ProfesionalDao
import cl.duoc.medicalconsulta.model.data.entities.ProfesionalEntity
import cl.duoc.medicalconsulta.model.data.repository.remote.ProfesionalRemoteRepository
import cl.duoc.medicalconsulta.model.domain.Profesional
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfesionalRepository(private val dao: ProfesionalDao) {

    private val remoteRepo = ProfesionalRemoteRepository()

    /**
     * Obtiene profesionales disponibles del backend y los sincroniza con la BD local
     */
    fun obtenerProfesionalesDisponibles(): Flow<List<ProfesionalEntity>> = flow {
        try {
            // Intentar obtener del backend
            val remoteProfesionales = remoteRepo.obtenerDisponibles()
            if (remoteProfesionales.isNotEmpty()) {
                // Limpiar y guardar en local
                dao.deleteAll()
                remoteProfesionales.forEach { profesional ->
                    dao.insertProfesional(profesional.toEntity())
                }
            }
        } catch (e: Exception) {
            Log.e("ProfesionalRepo", "Error sincronizando: ${e.message}")
        }
        // Emitir datos de la BD local (caché)
        dao.getProfesionalesDisponibles().collect { emit(it) }
    }

    /**
     * Obtiene todos los profesionales del backend y los sincroniza
     */
    fun obtenerTodosProfesionales(): Flow<List<ProfesionalEntity>> = flow {
        try {
            val remoteProfesionales = remoteRepo.obtenerTodos()
            if (remoteProfesionales.isNotEmpty()) {
                dao.deleteAll()
                remoteProfesionales.forEach { profesional ->
                    dao.insertProfesional(profesional.toEntity())
                }
            }
        } catch (e: Exception) {
            Log.e("ProfesionalRepo", "Error sincronizando: ${e.message}")
        }
        // Emitir desde caché local
        dao.getAllProfesionales().collect { emit(it) }
    }

    suspend fun obtenerProfesionalPorId(id: Long): ProfesionalEntity? {
        return try {
            // Intentar del backend primero
            val remote = remoteRepo.obtenerPorId(id)
            remote?.toEntity() ?: dao.getProfesionalById(id)
        } catch (e: Exception) {
            // Fallback a local
            dao.getProfesionalById(id)
        }
    }

    suspend fun guardarProfesional(profesional: ProfesionalEntity): Long {
        return try {
            // Guardar en backend
            val created = remoteRepo.crear(profesional.toDomain())
            created?.let {
                // Si se creó en backend, guardar en local
                dao.insertProfesional(it.toEntity())
            } ?: dao.insertProfesional(profesional)
        } catch (e: Exception) {
            // Fallback: guardar solo local
            dao.insertProfesional(profesional)
        }
    }

    suspend fun limpiar() = dao.deleteAll()

    // Mappers
    private fun Profesional.toEntity(): ProfesionalEntity {
        return ProfesionalEntity(
            id = this.id,
            nombre = this.nombre,
            especialidad = this.especialidad,
            disponible = this.disponible
        )
    }

    private fun ProfesionalEntity.toDomain(): Profesional {
        return Profesional(
            id = this.id,
            nombre = this.nombre,
            especialidad = this.especialidad,
            disponible = this.disponible
        )
    }
}