package cl.duoc.medicalconsulta.model.data.repository.remote

import android.util.Log
import cl.duoc.medicalconsulta.model.domain.Cita
import cl.duoc.medicalconsulta.network.RetrofitClient
import cl.duoc.medicalconsulta.network.dto.CitaDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CitaRemoteRepository {

    private val api = RetrofitClient.citaApiService

    suspend fun obtenerTodas(): List<Cita> = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerTodas()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                Log.e("CitaRepo", "Error ${response.code()}: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CitaRepo", "Exception: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun obtenerPorId(id: Long): Cita? = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerPorId(id)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("CitaRepo", "Error ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CitaRepo", "Exception: ${e.message}", e)
            null
        }
    }

    suspend fun obtenerPorRutPaciente(rut: String): List<Cita> = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerPorRutPaciente(rut)
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                Log.e("CitaRepo", "Error ${response.code()}: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CitaRepo", "Exception: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun crear(cita: Cita): Cita? = withContext(Dispatchers.IO) {
        try {
            val dto = cita.toDto()
            val response = api.crear(dto)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("CitaRepo", "Error ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CitaRepo", "Exception: ${e.message}", e)
            null
        }
    }

    suspend fun actualizar(id: Long, cita: Cita): Cita? = withContext(Dispatchers.IO) {
        try {
            val dto = cita.toDto()
            val response = api.actualizar(id, dto)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("CitaRepo", "Error ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CitaRepo", "Exception: ${e.message}", e)
            null
        }
    }

    suspend fun eliminar(id: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminar(id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CitaRepo", "Exception: ${e.message}", e)
            false
        }
    }

    // Mappers
    private fun CitaDto.toDomain(): Cita {
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

    private fun Cita.toDto(): CitaDto {
        return CitaDto(
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
