package cl.duoc.medicalconsulta.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API de OpenFDA
 */
data class FdaResponseDto(
    @SerializedName("results")
    val results: List<MedicamentoDto>?
)

/**
 * Información de un medicamento desde OpenFDA
 */
data class MedicamentoDto(
    @SerializedName("openfda")
    val openfda: OpenFdaDto?,

    @SerializedName("purpose")
    val purpose: List<String>?,

    @SerializedName("indications_and_usage")
    val indicationsAndUsage: List<String>?,

    @SerializedName("warnings")
    val warnings: List<String>?,

    @SerializedName("active_ingredient")
    val activeIngredient: List<String>?
)

/**
 * Metadatos de OpenFDA
 */
data class OpenFdaDto(
    @SerializedName("brand_name")
    val brandName: List<String>?,

    @SerializedName("generic_name")
    val genericName: List<String>?,

    @SerializedName("manufacturer_name")
    val manufacturerName: List<String>?,

    @SerializedName("product_type")
    val productType: List<String>?,

    @SerializedName("route")
    val route: List<String>?
)
