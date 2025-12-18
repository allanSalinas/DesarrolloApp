package cl.duoc.medicalconsulta.model.domain

data class Medicamento(
    val nombreComercial: String,
    val nombreGenerico: String,
    val fabricante: String,
    val proposito: String,
    val indicaciones: String,
    val advertencias: String,
    val ingredienteActivo: String,
    val viaAdministracion: String
)
