package cl.duoc.medicalconsulta.model.data.repository.remote

import android.util.Log
import cl.duoc.medicalconsulta.model.domain.Profesional
import cl.duoc.medicalconsulta.network.RetrofitClient
import cl.duoc.medicalconsulta.network.dto.ProfesionalDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfesionalRemoteRepository {

    private val api = RetrofitClient.profesionalApiService

    suspend fun obtenerTodos(): List<Profesional> = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerTodos()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                Log.e("ProfesionalRepo", "Error ${response.code()}: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProfesionalRepo", "Exception: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun obtenerDisponibles(): List<Profesional> = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerDisponibles()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                Log.e("ProfesionalRepo", "Error ${response.code()}: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProfesionalRepo", "Exception: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun obtenerPorId(id: Long): Profesional? = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerPorId(id)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("ProfesionalRepo", "Error ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProfesionalRepo", "Exception: ${e.message}", e)
            null
        }
    }

    suspend fun crear(profesional: Profesional): Profesional? = withContext(Dispatchers.IO) {
        try {
            val dto = profesional.toDto()
            val response = api.crear(dto)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("ProfesionalRepo", "Error ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProfesionalRepo", "Exception: ${e.message}", e)
            null
        }
    }

    suspend fun actualizar(id: Long, profesional: Profesional): Profesional? = withContext(Dispatchers.IO) {
        try {
            val dto = profesional.toDto()
            val response = api.actualizar(id, dto)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("ProfesionalRepo", "Error ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProfesionalRepo", "Exception: ${e.message}", e)
            null
        }
    }

    suspend fun eliminar(id: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminar(id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ProfesionalRepo", "Exception: ${e.message}", e)
            false
        }
    }

    // Mappers
    private fun ProfesionalDto.toDomain(): Profesional {
        return Profesional(
            id = this.id,
            nombre = this.nombre,
            especialidad = this.especialidad,
            disponible = this.disponible
        )
    }

    private fun Profesional.toDto(): ProfesionalDto {
        return ProfesionalDto(
            id = this.id,
            nombre = this.nombre,
            especialidad = this.especialidad,
            disponible = this.disponible
        )
    }
}
