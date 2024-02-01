package com.minizuure.todoplannereducationedition.services.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Base interface for all DAOs.
 * @param T The type of the entity.
 * insert, delete, update are all suspend functions.
 */
@Dao
interface BaseIODao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg obj: T) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T) : Long

    @Delete
    suspend fun delete(vararg obj: T)

    @Update
    suspend fun update(vararg obj: T)

}