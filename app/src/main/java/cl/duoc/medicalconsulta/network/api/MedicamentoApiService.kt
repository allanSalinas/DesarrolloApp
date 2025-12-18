package cl.duoc.medicalconsulta.network.api

import cl.duoc.medicalconsulta.network.dto.FdaResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API Service para OpenFDA Drug Label API
 * Documentación: https://open.fda.gov/apis/drug/label/
 */
interface MedicamentoApiService {

    /**
     * Buscar medicamentos por nombre
     * @param search Query de búsqueda (ej: "openfda.brand_name:aspirina")
     * @param limit Límite de resultados (default: 10)
     */
    @GET("drug/label.json")
    suspend fun buscarMedicamentos(
        @Query("search") search: String,
        @Query("limit") limit: Int = 10
    ): Response<FdaResponseDto>

    /**
     * Buscar medicamento por nombre genérico
     */
    @GET("drug/label.json")
    suspend fun buscarPorNombreGenerico(
        @Query("search") genericName: String,
        @Query("limit") limit: Int = 5
    ): Response<FdaResponseDto>
}
