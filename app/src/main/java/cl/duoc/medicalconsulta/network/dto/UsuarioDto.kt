package cl.duoc.medicalconsulta.network.dto

import cl.duoc.medicalconsulta.model.data.entities.UsuarioEntity
import cl.duoc.medicalconsulta.model.domain.Rol
import cl.duoc.medicalconsulta.model.domain.Usuario
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object para comunicación con el backend.
 * Representa la estructura JSON que se envía/recibe del API REST.
 */
data class UsuarioDto(
    @SerializedName("id")
    val id: Long = 0L,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String? = null, // Solo se usa en registro, no en respuestas

    @SerializedName("nombreCompleto")
    val nombreCompleto: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("rut")
    val rut: String,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("fotoPerfil")
    val fotoPerfil: String? = null,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("activo")
    val activo: Boolean = true,

    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null,

    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String? = null
) {
    /**
     * Convierte el DTO al modelo de dominio
     */
    fun toDomain(): Usuario {
        return Usuario(
            id = id,
            username = username,
            nombreCompleto = nombreCompleto,
            email = email,
            rut = rut,
            rol = Rol.fromString(rol) ?: Rol.PACIENTE,
            fotoPerfil = fotoPerfil,
            telefono = telefono,
            activo = activo
        )
    }

    /**
     * Convierte el DTO a entidad de base de datos
     */
    fun toEntity(): UsuarioEntity {
        return UsuarioEntity(
            id = id,
            username = username,
            nombreCompleto = nombreCompleto,
            email = email,
            rut = rut,
            rol = rol,
            fotoPerfil = fotoPerfil,
            telefono = telefono,
            activo = activo
        )
    }

    companion object {
        /**
         * Crea un DTO desde el modelo de dominio
         */
        fun fromDomain(usuario: Usuario, password: String? = null): UsuarioDto {
            return UsuarioDto(
                id = usuario.id,
                username = usuario.username,
                password = password,
                nombreCompleto = usuario.nombreCompleto,
                email = usuario.email,
                rut = usuario.rut,
                rol = usuario.rol.name,
                fotoPerfil = usuario.fotoPerfil,
                telefono = usuario.telefono,
                activo = usuario.activo
            )
        }

        /**
         * Crea un DTO desde la entidad de base de datos
         */
        fun fromEntity(entity: UsuarioEntity, password: String? = null): UsuarioDto {
            return UsuarioDto(
                id = entity.id,
                username = entity.username,
                password = password,
                nombreCompleto = entity.nombreCompleto,
                email = entity.email,
                rut = entity.rut,
                rol = entity.rol,
                fotoPerfil = entity.fotoPerfil,
                telefono = entity.telefono,
                activo = entity.activo
            )
        }
    }
}
