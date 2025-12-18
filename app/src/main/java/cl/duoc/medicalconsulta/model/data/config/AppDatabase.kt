package cl.duoc.medicalconsulta.model.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.medicalconsulta.model.data.dao.CitaDao
import cl.duoc.medicalconsulta.model.data.dao.ProfesionalDao
import cl.duoc.medicalconsulta.model.data.dao.UsuarioDao
import cl.duoc.medicalconsulta.model.data.entities.CitaEntity
import cl.duoc.medicalconsulta.model.data.entities.ProfesionalEntity
import cl.duoc.medicalconsulta.model.data.entities.UsuarioEntity

/**
 * Base de datos principal de la aplicación Medical Consulta.
 * Utiliza Room para persistencia local de datos.
 *
 * Entidades incluidas:
 * - UsuarioEntity: Usuarios del sistema
 * - ProfesionalEntity: Profesionales médicos
 * - CitaEntity: Citas médicas
 */
@Database(
    entities = [
        UsuarioEntity::class,
        ProfesionalEntity::class,
        CitaEntity::class
    ],
    version = 2, // Incrementada por la adición de UsuarioEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun profesionalDao(): ProfesionalDao
    abstract fun citaDao(): CitaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medical_consulta_db"
                )
                    .fallbackToDestructiveMigration() // NOTA: En producción, usar migraciones apropiadas
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}