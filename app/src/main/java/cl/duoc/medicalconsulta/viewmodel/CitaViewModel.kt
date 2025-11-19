package cl.duoc.medicalconsulta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.medicalconsulta.model.data.entities.CitaEntity
import cl.duoc.medicalconsulta.model.data.repository.CitaRepository
import cl.duoc.medicalconsulta.model.data.repository.ProfesionalRepository
import cl.duoc.medicalconsulta.model.domain.CitaErrores
import cl.duoc.medicalconsulta.model.domain.CitaUIState
import cl.duoc.medicalconsulta.model.domain.Profesional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CitaViewModel(
    private val citaRepository: CitaRepository,
    private val profesionalRepository: ProfesionalRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(CitaUIState())
    val estado: StateFlow<CitaUIState> = _estado.asStateFlow()

    private val _profesionales = MutableStateFlow<List<Profesional>>(emptyList())
    val profesionales: StateFlow<List<Profesional>> = _profesionales.asStateFlow()

    private val _historialCitas = MutableStateFlow<List<CitaEntity>>(emptyList())
    val historialCitas: StateFlow<List<CitaEntity>> = _historialCitas.asStateFlow()

    init {
        cargarProfesionales()
        cargarHistorialCitas()
    }

    private fun cargarProfesionales() {
        viewModelScope.launch {
            profesionalRepository.obtenerProfesionalesDisponibles().collect { entities ->
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

    private fun cargarHistorialCitas() {
        viewModelScope.launch {
            citaRepository.obtenerTodasCitas().collect { citas ->
                _historialCitas.value = citas
            }
        }
    }

    fun onNombreChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                pacienteNombre = valor,
                errores = actual.errores.copy(
                    pacienteNombre = if (valor.isBlank())
                        "El nombre es obligatorio"
                    else null
                )
            )
        }
    }

    fun onRutChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                pacienteRut = valor,
                errores = actual.errores.copy(
                    pacienteRut = when {
                        valor.isBlank() -> "El RUT es obligatorio"
                        !RUT_REGEX.matches(valor) -> "Formato de RUT inválido (ej: 12345678-9)"
                        else -> null
                    }
                )
            )
        }
    }

    fun onProfesionalSeleccionado(profesional: Profesional) {
        _estado.update { actual ->
            actual.copy(
                profesionalSeleccionado = profesional,
                errores = actual.errores.copy(profesional = null)
            )
        }
    }

    fun onFechaChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                fecha = valor,
                errores = actual.errores.copy(
                    fecha = when {
                        valor.isBlank() -> "La fecha es obligatoria"
                        !FECHA_REGEX.matches(valor) -> "Formato inválido (dd/mm/yyyy)"
                        else -> null
                    }
                )
            )
        }
    }

    fun onHoraChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                hora = valor,
                errores = actual.errores.copy(
                    hora = when {
                        valor.isBlank() -> "La hora es obligatoria"
                        !HORA_REGEX.matches(valor) -> "Formato inválido (HH:MM)"
                        else -> null
                    }
                )
            )
        }
    }

    fun onMotivoChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                motivoConsulta = valor,
                errores = actual.errores.copy(
                    motivoConsulta = if (valor.isBlank())
                        "El motivo de consulta es obligatorio"
                    else null
                )
            )
        }
    }

    fun onAgendarCita() {
        val ui = _estado.value

        // Validar todos los campos
        val errores = CitaErrores(
            pacienteNombre = if (ui.pacienteNombre.isBlank())
                "El nombre es obligatorio" else null,
            pacienteRut = when {
                ui.pacienteRut.isBlank() -> "El RUT es obligatorio"
                !RUT_REGEX.matches(ui.pacienteRut) -> "Formato de RUT inválido"
                else -> null
            },
            profesional = if (ui.profesionalSeleccionado == null)
                "Debe seleccionar un profesional" else null,
            fecha = when {
                ui.fecha.isBlank() -> "La fecha es obligatoria"
                !FECHA_REGEX.matches(ui.fecha) -> "Formato inválido (dd/mm/yyyy)"
                else -> null
            },
            hora = when {
                ui.hora.isBlank() -> "La hora es obligatoria"
                !HORA_REGEX.matches(ui.hora) -> "Formato inválido (HH:MM)"
                else -> null
            },
            motivoConsulta = if (ui.motivoConsulta.isBlank())
                "El motivo es obligatorio" else null
        )

        // Actualizar errores
        _estado.update { it.copy(errores = errores) }

        // Si hay errores, no continuar
        if (errores.tieneErrores()) return

        // Guardar cita en la base de datos
        viewModelScope.launch {
            val profesional = ui.profesionalSeleccionado!!
            val entity = CitaEntity(
                pacienteNombre = ui.pacienteNombre,
                pacienteRut = ui.pacienteRut,
                profesionalId = profesional.id,
                profesionalNombre = profesional.nombre,
                especialidad = profesional.especialidad,
                fecha = ui.fecha,
                hora = ui.hora,
                motivoConsulta = ui.motivoConsulta
            )

            citaRepository.guardarCita(entity)

            // Resetear formulario y mostrar éxito
            _estado.update {
                CitaUIState(guardadoExitoso = true)
            }

            // Después de 2 segundos, quitar el mensaje de éxito
            kotlinx.coroutines.delay(2000)
            _estado.update { it.copy(guardadoExitoso = false) }
        }
    }

    companion object {
        private val RUT_REGEX = "^[0-9]{7,8}-[0-9Kk]$".toRegex()
        private val FECHA_REGEX = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/[0-9]{4}$".toRegex()
        private val HORA_REGEX = "^([01][0-9]|2[0-3]):[0-5][0-9]$".toRegex()
    }
}