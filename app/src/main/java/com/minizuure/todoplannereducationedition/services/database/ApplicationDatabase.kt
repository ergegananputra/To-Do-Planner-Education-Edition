package com.minizuure.todoplannereducationedition.services.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTableDao
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionTableDao
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskTableDao

@Database(
    entities = [
        RoutineTable::class,
        SessionTable::class,
        TaskTable::class
               ],
    version = 1,
    exportSchema = true
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun routineTableDao(): RoutineTableDao
    abstract fun sessionTableDao(): SessionTableDao
    abstract fun taskTableDao(): TaskTableDao

    abstract fun deleteAllOperation(): DeleteAllOperation


    companion object {
        private const val DATABASE_NAME = "todo_planner_education_edition_database"

        @Volatile
        private var INSTANCE : ApplicationDatabase? = null

        fun getDatabase(context: Context): ApplicationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    ApplicationDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}