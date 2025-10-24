package com.consultamedica.model

import java.util.Date

data class Appointment(
    val id: String = "",
    val userId: String = "",
    val specialistId: String = "",
    val date: Date = Date(),
    val status: String = ""
)