package cl.duoc.medicalconsulta.model.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.medicalconsulta.model.data.entities.ProfesionalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesionalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfesional(profesional: ProfesionalEntity): Long

    @Query("SELECT * FROM profesionales WHERE disponible = 1 ORDER BY nombre ASC")
    fun getProfesionalesDisponibles(): Flow<List<ProfesionalEntity>>

    @Query("SELECT * FROM profesionales ORDER BY nombre ASC")
    fun getAllProfesionales(): Flow<List<ProfesionalEntity>>

    @Query("SELECT * FROM profesionales WHERE id = :id")
    suspend fun getProfesionalById(id: Long): ProfesionalEntity?

    @Query("DELETE FROM profesionales")
    suspend fun deleteAll()
}