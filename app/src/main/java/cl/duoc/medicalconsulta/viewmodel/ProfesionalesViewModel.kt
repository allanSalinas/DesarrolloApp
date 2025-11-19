package cl.duoc.medicalconsulta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.medicalconsulta.model.data.repository.ProfesionalRepository
import cl.duoc.medicalconsulta.model.domain.Profesional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfesionalesViewModel(
    private val repository: ProfesionalRepository
) : ViewModel() {

    private val _profesionales = MutableStateFlow<List<Profesional>>(emptyList())
    val profesionales: StateFlow<List<Profesional>> = _profesionales.asStateFlow()

    init {
        cargarProfesionales()
    }

    private fun cargarProfesionales() {
        viewModelScope.launch {
            repository.obtenerProfesionalesDisponibles().collect { entities ->
                _profesionales.value = entities.map { entity ->
                    Profesional(
                        id = entity.id,
                        nombre = entity.nombre,
                        especialidad = entity.especialidad,
                        disponible = entity.disponible
                    )
                }
            }
        }
    }
}