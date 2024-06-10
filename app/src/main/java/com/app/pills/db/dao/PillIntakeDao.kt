package com.app.pills.db.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.pills.db.entity.PillIntakeEntity
import com.app.pills.db.entity.PillIntakePillEntity
import com.app.pills.db.entity.PillIntakeWithDetails

@Dao
interface PillIntakeDao {
    @Insert
    suspend fun insert(pillIntake: PillIntakeEntity): Long

    @Insert
    suspend fun insertPillIntakePill(pillIntakePill: PillIntakePillEntity)

    @Update
    suspend fun update(pillIntake: PillIntakeEntity)

    @Delete
    suspend fun delete(pillIntake: PillIntakeEntity)

    @Query("SELECT * FROM PILL_INTAKES")
    fun getAllPillIntakes(): LiveData<List<PillIntakeEntity>>

    @Transaction
    @Query("SELECT * FROM pill_intakes")
    suspend fun getPillIntakeWithDetails(): List<PillIntakeWithDetails>

    @Query("DELETE FROM pill_intakes WHERE is_done = 0")
    suspend fun deleteUncompleted()

    @Transaction
    @Query("SELECT * FROM pill_intakes WHERE id = :pillIntakeId")
    suspend fun getPillIntakeById(pillIntakeId: Int): PillIntakeWithDetails?
}