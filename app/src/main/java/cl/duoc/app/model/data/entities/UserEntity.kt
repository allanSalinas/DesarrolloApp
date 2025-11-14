package cl.duoc.app.model.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "nombre_completo")
    val nombreCompleto: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "tipo_usuario")
    val tipoUsuario: String, // "PACIENTE" o "MEDICO"

    @ColumnInfo(name = "telefono")
    val telefono: String? = null,

    @ColumnInfo(name = "fecha_nacimiento")
    val fechaNacimiento: String? = null,

    @ColumnInfo(name = "rut")
    val rut: String? = null,

    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "activo")
    val activo: Boolean = true
)