package cl.duoc.medicalconsulta.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.medicalconsulta.model.data.config.AppDatabase
import cl.duoc.medicalconsulta.model.data.repository.ProfesionalRepository

class ProfesionalesViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfesionalesViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application)
            val repo = ProfesionalRepository(db.profesionalDao())
            @Suppress("UNCHECKED_CAST")
            return ProfesionalesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
