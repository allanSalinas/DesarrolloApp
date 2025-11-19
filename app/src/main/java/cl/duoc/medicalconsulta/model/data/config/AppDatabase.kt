package cl.duoc.medicalconsulta.model.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.medicalconsulta.model.data.dao.CitaDao
import cl.duoc.medicalconsulta.model.data.dao.ProfesionalDao
import cl.duoc.medicalconsulta.model.data.entities.CitaEntity
import cl.duoc.medicalconsulta.model.data.entities.ProfesionalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.profesionalDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(profesionalDao: ProfesionalDao) {
            // Datos iniciales de profesionales
            val profesionales = listOf(
                ProfesionalEntity(
                    id = 1,
                    nombre = "Dr. Carlos Martínez",
                    especialidad = "Medicina General",
                    disponible = true
                ),
                ProfesionalEntity(
                    id = 2,
                    nombre = "Dra. Ana López",
                    especialidad = "Cardiología",
                    disponible = true
                ),
                ProfesionalEntity(
                    id = 3,
                    nombre = "Dr. Roberto Silva",
                    especialidad = "Pediatría",
                    disponible = true
                ),
                ProfesionalEntity(
                    id = 4,
                    nombre = "Dra. María Fernández",
                    especialidad = "Dermatología",
                    disponible = true
                ),
                ProfesionalEntity(
                    id = 5,
                    nombre = "Dr. Jorge Ramírez",
                    especialidad = "Traumatología",
                    disponible = true
                )
            )

            profesionales.forEach { profesionalDao.insertProfesional(it) }
        }
    }
}