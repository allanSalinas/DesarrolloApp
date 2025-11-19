package cl.duoc.medicalconsulta.model.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.medicalconsulta.model.data.entities.CitaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCita(cita: CitaEntity): Long

    @Query("SELECT * FROM citas ORDER BY timestamp DESC")
    fun getAllCitas(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE id = :id")
    suspend fun getCitaById(id: Long): CitaEntity?

    @Query("DELETE FROM citas WHERE id = :id")
    suspend fun deleteCita(id: Long)

    @Query("DELETE FROM citas")
    suspend fun deleteAll()
}