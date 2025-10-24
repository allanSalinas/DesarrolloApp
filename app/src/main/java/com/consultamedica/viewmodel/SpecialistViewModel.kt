package com.consultamedica.viewmodel

import androidx.lifecycle.ViewModel
import com.consultamedica.model.Specialist

class SpecialistViewModel : ViewModel() {

    // Datos de ejemplo
    val specialists = listOf(
        Specialist(id = "1", name = "Dr. Juan Pérez", specialty = "Cardiología"),
        Specialist(id = "2", name = "Dra. Ana Gómez", specialty = "Dermatología"),
        Specialist(id = "3", name = "Dr. Luis Ramos", specialty = "Pediatría")
    )

    // Aquí iría la lógica para obtener los especialistas desde una base de datos o API
}
