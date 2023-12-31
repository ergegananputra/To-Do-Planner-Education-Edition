package com.minizuure.todoplannereducationedition.services.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Base interface for all DAOs.
 * @param T The type of the entity.
 * insert, delete, update are all suspend functions.
 */
interface BaseIODao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg obj: T)

    @Delete
    suspend fun delete(vararg obj: T)

    @Update
    suspend fun update(vararg obj: T)

}