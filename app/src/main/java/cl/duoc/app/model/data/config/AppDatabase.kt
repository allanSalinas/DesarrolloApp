package cl.duoc.app.model.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.app.model.data.dao.AppointmentDao
import cl.duoc.app.model.data.dao.UserDao
import cl.duoc.app.model.data.entities.AppointmentEntity
import cl.duoc.app.model.data.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        AppointmentEntity::class     // ← AGREGADO
    ],
    version = 2,              // ← VERSIÓN INCREMENTADA
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun appointmentDao(): AppointmentDao // ← AGREGADO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "desarrollo_app_database" // Nombre de la BD cambiado para evitar conflictos
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
