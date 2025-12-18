package cl.duoc.medicalconsulta.model.data.repository.remote

import android.util.Log
import cl.duoc.medicalconsulta.model.domain.Medicamento
import cl.duoc.medicalconsulta.network.OpenFdaClient
import cl.duoc.medicalconsulta.network.dto.MedicamentoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MedicamentoRepository {

    private val api = OpenFdaClient.medicamentoApiService

    /**
     * Buscar medicamentos por nombre (comercial o genérico)
     */
    suspend fun buscarMedicamentos(nombre: String): List<Medicamento> = withContext(Dispatchers.IO) {
        try {
            // Buscar por nombre comercial o genérico
            val searchQuery = "openfda.brand_name:$nombre OR openfda.generic_name:$nombre"
            val response = api.buscarMedicamentos(searchQuery, limit = 10)

            if (response.isSuccessful) {
                response.body()?.results?.mapNotNull { it.toDomain() } ?: emptyList()
            } else {
                Log.e("MedicamentoRepo", "Error ${response.code()}: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MedicamentoRepo", "Exception: ${e.message}", e)
            emptyList()
        }
    }

    // Mapper de DTO a Domain
    private fun MedicamentoDto.toDomain(): Medicamento? {
        return try {
            Medicamento(
                nombreComercial = openfda?.brandName?.firstOrNull() ?: "No disponible",
                nombreGenerico = openfda?.genericName?.firstOrNull() ?: "No disponible",
                fabricante = openfda?.manufacturerName?.firstOrNull() ?: "No disponible",
                proposito = purpose?.firstOrNull() ?: "No disponible",
                indicaciones = indicationsAndUsage?.firstOrNull()?.take(300) ?: "No disponible",
                advertencias = warnings?.firstOrNull()?.take(300) ?: "No disponible",
                ingredienteActivo = activeIngredient?.firstOrNull() ?: openfda?.genericName?.firstOrNull() ?: "No disponible",
                viaAdministracion = openfda?.route?.firstOrNull() ?: "No disponible"
            )
        } catch (e: Exception) {
            Log.e("MedicamentoRepo", "Error mapping: ${e.message}")
            null
        }
    }
}
