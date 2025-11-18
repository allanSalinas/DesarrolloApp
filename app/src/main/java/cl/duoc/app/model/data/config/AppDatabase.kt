package cl.duoc.app.model.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.app.model.data.dao.AppointmentDao
import cl.duoc.app.model.data.dao.ProfessionalDao
import cl.duoc.app.model.data.dao.UserDao
import cl.duoc.app.model.data.entities.AppointmentEntity
import cl.duoc.app.model.data.entities.ProfessionalEntity
import cl.duoc.app.model.data.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        AppointmentEntity::class,
        ProfessionalEntity::class
    ],
    version = 4, // 👈 VERSIÓN INCREMENTADA A 4
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun professionalDao(): ProfessionalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "desarrollo_app_database"
                )
                    .fallbackToDestructiveMigration() // NOTA: Usar con cuidado en producción
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
