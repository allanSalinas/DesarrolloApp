package cl.duoc.medicalconsulta.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.medicalconsulta.model.data.config.AppDatabase
import cl.duoc.medicalconsulta.model.data.repository.CitaRepository
import cl.duoc.medicalconsulta.model.data.repository.ProfesionalRepository

class CitaViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitaViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application)
            val citaRepo = CitaRepository(db.citaDao())
            val profesionalRepo = ProfesionalRepository(db.profesionalDao())
            @Suppress("UNCHECKED_CAST")
            return CitaViewModel(citaRepo, profesionalRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}