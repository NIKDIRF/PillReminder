package com.app.pills.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.pills.db.entity.PillEntity


@Dao
interface PillDao {
    @Insert
    suspend fun insertPill(pill: PillEntity)

    @Update
    suspend fun updatePill(pill: PillEntity)

    @Delete
    suspend fun deletePill(pill: PillEntity)

    @Query("SELECT * FROM pills")
    fun getAllPills(): LiveData<List<PillEntity>>

    @Query("SELECT * FROM pills WHERE id = :id")
    fun getPillById(id: Int): PillEntity

    @Query("UPDATE pills SET count = count - :dosage WHERE name = :name AND count >= :dosage")
    suspend fun updatePillByName(name: String, dosage: Int)

    @Query("SELECT count FROM pills WHERE name = :name")
    fun getPillCountByName(name: String): Int?
}