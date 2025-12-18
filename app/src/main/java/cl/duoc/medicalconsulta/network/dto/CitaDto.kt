package cl.duoc.medicalconsulta.network.dto

import com.google.gson.annotations.SerializedName

data class CitaDto(
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("pacienteNombre")
    val pacienteNombre: String,

    @SerializedName("pacienteRut")
    val pacienteRut: String,

    @SerializedName("profesionalId")
    val profesionalId: Long,

    @SerializedName("profesionalNombre")
    val profesionalNombre: String,

    @SerializedName("especialidad")
    val especialidad: String,

    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("hora")
    val hora: String,

    @SerializedName("motivoConsulta")
    val motivoConsulta: String,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
