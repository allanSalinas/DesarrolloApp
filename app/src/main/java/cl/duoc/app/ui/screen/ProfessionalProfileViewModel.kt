package cl.duoc.app.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.ObtenerPerfilProfesionalUseCase
import cl.duoc.app.model.data.entities.ProfessionalEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfessionalProfileViewModel(
    private val obtenerPerfilUseCase: ObtenerPerfilProfesionalUseCase,
    private val professionalId: Int
) : ViewModel() {

    private val _profesional = MutableStateFlow<ProfessionalEntity?>(null)
    val profesional: StateFlow<ProfessionalEntity?> = _profesional

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        viewModelScope.launch {
            obtenerPerfilUseCase(professionalId)
                .onSuccess { _profesional.value = it }
                .onFailure { _error.value = it.message }
        }
    }
}