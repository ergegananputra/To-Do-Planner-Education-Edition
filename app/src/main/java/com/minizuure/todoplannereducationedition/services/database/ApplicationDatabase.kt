package com.minizuure.todoplannereducationedition.services.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskDao
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskTable
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteDao
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteTable
import com.minizuure.todoplannereducationedition.services.database.queue.NotificationQueueTable
import com.minizuure.todoplannereducationedition.services.database.queue.NotificationQueueTableDao
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderTable
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderTableDao
import com.minizuure.todoplannereducationedition.services.database.reservasion.ReservationTable
import com.minizuure.todoplannereducationedition.services.database.reservasion.ReservationTableDao
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
        TaskTable::class,
        TodoNoteTable::class,
        NotesTaskTable::class,
        NotificationQueueTable::class,
        SessionTaskProviderTable::class,
        ReservationTable::class
               ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4)
    ]
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun routineTableDao(): RoutineTableDao
    abstract fun sessionTableDao(): SessionTableDao
    abstract fun taskTableDao(): TaskTableDao
    abstract fun todoNoteTableDao(): TodoNoteDao
    abstract fun notesTaskTableDao(): NotesTaskDao
    abstract fun notificationQueueTableDao(): NotificationQueueTableDao
    abstract fun sessionTaskProviderTableDao(): SessionTaskProviderTableDao

    abstract fun deleteAllOperation(): DeleteAllOperation

    abstract fun reservationTableDao(): ReservationTableDao


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
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }


}