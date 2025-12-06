package cl.duoc.medicalconsulta.model.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.medicalconsulta.model.data.dao.CitaDao
import cl.duoc.medicalconsulta.model.data.dao.ProfesionalDao
import cl.duoc.medicalconsulta.model.data.entities.CitaEntity
import cl.duoc.medicalconsulta.model.data.entities.ProfesionalEntity

@Database(
    entities = [ProfesionalEntity::class, CitaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}