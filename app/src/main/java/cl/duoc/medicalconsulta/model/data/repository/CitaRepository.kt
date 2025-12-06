package cl.duoc.medicalconsulta.model.data.repository

import android.util.Log
import cl.duoc.medicalconsulta.model.data.dao.CitaDao
import cl.duoc.medicalconsulta.model.data.entities.CitaEntity
import cl.duoc.medicalconsulta.model.data.repository.remote.CitaRemoteRepository
import cl.duoc.medicalconsulta.model.domain.Cita
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CitaRepository(private val dao: CitaDao) {

    private val remoteRepo = CitaRemoteRepository()

    /**
     * Obtiene todas las citas del backend y las sincroniza con la BD local
     */
    fun obtenerTodasCitas(): Flow<List<CitaEntity>> = flow {
        try {
            val remoteCitas = remoteRepo.obtenerTodas()
            if (remoteCitas.isNotEmpty()) {
                dao.deleteAll()
                remoteCitas.forEach { cita ->
                    dao.insertCita(cita.toEntity())
                }
            }
        } catch (e: Exception) {
            Log.e("CitaRepo", "Error sincronizando: ${e.message}")
        }
        // Emitir desde caché local
        dao.getAllCitas().collect { emit(it) }
    }

    suspend fun obtenerCitaPorId(id: Long): CitaEntity? {
        return try {
            val remote = remoteRepo.obtenerPorId(id)
            remote?.toEntity() ?: dao.getCitaById(id)
        } catch (e: Exception) {
            dao.getCitaById(id)
        }
    }

    suspend fun guardarCita(cita: CitaEntity): Long {
        return try {
            val created = remoteRepo.crear(cita.toDomain())
            created?.let {
                dao.insertCita(it.toEntity())
            } ?: dao.insertCita(cita)
        } catch (e: Exception) {
            dao.insertCita(cita)
        }
    }

    suspend fun actualizarCita(cita: CitaEntity) {
        try {
            remoteRepo.actualizar(cita.id, cita.toDomain())
            dao.updateCita(cita)
        } catch (e: Exception) {
            dao.updateCita(cita)
        }
    }

    suspend fun eliminarCita(id: Long) {
        try {
            remoteRepo.eliminar(id)
            dao.deleteCita(id)
        } catch (e: Exception) {
            dao.deleteCita(id)
        }
    }

    suspend fun limpiar() = dao.deleteAll()

    // Mappers
    private fun Cita.toEntity(): CitaEntity {
        return CitaEntity(
            id = this.id,
            pacienteNombre = this.pacienteNombre,
            pacienteRut = this.pacienteRut,
            profesionalId = this.profesionalId,
            profesionalNombre = this.profesionalNombre,
            especialidad = this.especialidad,
            fecha = this.fecha,
            hora = this.hora,
            motivoConsulta = this.motivoConsulta,
            timestamp = this.timestamp
        )
    }

    private fun CitaEntity.toDomain(): Cita {
        return Cita(
            id = this.id,
            pacienteNombre = this.pacienteNombre,
            pacienteRut = this.pacienteRut,
            profesionalId = this.profesionalId,
            profesionalNombre = this.profesionalNombre,
            especialidad = this.especialidad,
            fecha = this.fecha,
            hora = this.hora,
            motivoConsulta = this.motivoConsulta,
            timestamp = this.timestamp
        )
    }
}