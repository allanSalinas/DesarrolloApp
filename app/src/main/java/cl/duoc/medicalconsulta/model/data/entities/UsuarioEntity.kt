package cl.duoc.medicalconsulta.model.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.duoc.medicalconsulta.model.domain.Rol
import cl.duoc.medicalconsulta.model.domain.Usuario

/**
 * Entidad de Room Database para persistir usuarios localmente.
 * Representa la tabla 'usuarios' en la base de datos local.
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "nombre_completo")
    val nombreCompleto: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "rut")
    val rut: String,

    @ColumnInfo(name = "rol")
    val rol: String, // Guardado como String para compatibilidad con Room

    @ColumnInfo(name = "foto_perfil")
    val fotoPerfil: String? = null,

    @ColumnInfo(name = "telefono")
    val telefono: String? = null,

    @ColumnInfo(name = "activo")
    val activo: Boolean = true,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "ultima_actualizacion")
    val ultimaActualizacion: Long = System.currentTimeMillis()
) {
    /**
     * Convierte la entidad de base de datos al modelo de dominio
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

    companion object {
        /**
         * Crea una entidad desde el modelo de dominio
         */
        fun fromDomain(usuario: Usuario): UsuarioEntity {
            return UsuarioEntity(
                id = usuario.id,
                username = usuario.username,
                nombreCompleto = usuario.nombreCompleto,
                email = usuario.email,
                rut = usuario.rut,
                rol = usuario.rol.name,
                fotoPerfil = usuario.fotoPerfil,
                telefono = usuario.telefono,
                activo = usuario.activo,
                ultimaActualizacion = System.currentTimeMillis()
            )
        }
    }
}
