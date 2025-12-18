package cl.duoc.medicalconsulta.network.dto

import com.google.gson.annotations.SerializedName

data class ProfesionalDto(
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("especialidad")
    val especialidad: String,

    @SerializedName("disponible")
    val disponible: Boolean = true
)
