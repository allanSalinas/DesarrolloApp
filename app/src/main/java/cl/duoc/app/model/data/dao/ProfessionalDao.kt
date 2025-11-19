package cl.duoc.app.model.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.app.model.data.entities.ProfessionalEntity

@Dao
interface ProfessionalDao {

    @Query("SELECT * FROM professionals")
    suspend fun getAllProfessionals(): List<ProfessionalEntity>

    @Query("SELECT * FROM professionals WHERE id = :id")
    suspend fun getProfessionalById(id: Int): ProfessionalEntity?

    @Insert
    suspend fun insertProfessional(professional: ProfessionalEntity)
}