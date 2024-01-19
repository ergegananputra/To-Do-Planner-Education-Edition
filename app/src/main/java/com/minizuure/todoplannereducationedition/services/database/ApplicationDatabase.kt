package com.minizuure.todoplannereducationedition.services.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskDao
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskTable
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteDao
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteTable
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
        NotesTaskTable::class
               ],
    version = 5,
    exportSchema = true
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun routineTableDao(): RoutineTableDao
    abstract fun sessionTableDao(): SessionTableDao
    abstract fun taskTableDao(): TaskTableDao
    abstract fun todoNoteTableDao(): TodoNoteDao
    abstract fun notesTaskTableDao(): NotesTaskDao

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
                )
                    .fallbackToDestructiveMigration() // change to this => .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()

                INSTANCE = instance
                instance
            }
        }
//
//        /**
//         * Migration from:
//         * version 1 -> version 2
//         * Add isSharedToCommunity and communityId column to routine_table
//         */
//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("ALTER TABLE routine_table ADD COLUMN isSharedToCommunity INTEGER NOT NULL DEFAULT 0")
//                db.execSQL("ALTER TABLE routine_table ADD COLUMN communityId TEXT")
//            }
//        }
//
//
//        /**
//         * Migration from:
//         * version 2 -> version 3
//         * Add TodoNoteTable and NotesTaskTable
//         * Add date_iso8601 and updated_at column to task_table
//         */
//        private val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL(
//                    "CREATE TABLE todo_note_table (" +
//                            "id INTEGER PRIMARY KEY NOT NULL, " +
//                            "fk_notes_task_id INTEGER NOT NULL, " +
//                            "is_checked INTEGER NOT NULL DEFAULT 0, " +
//                            "description TEXT NOT NULL, " +
//                            "updated_at INTEGER NOT NULL, " +
//                            "FOREIGN KEY(fk_notes_task_id) REFERENCES notes_task_table(id) ON DELETE CASCADE)"
//                )
//                db.execSQL(
//                    "CREATE TABLE notes_task_table (" +
//                            "id INTEGER PRIMARY KEY NOT NULL, " +
//                            "fk_task_id INTEGER NOT NULL, " +
//                            "category TEXT NOT NULL, " +
//                            "description TEXT NOT NULL, " +
//                            "updated_at INTEGER NOT NULL, " +
//                            "FOREIGN KEY(fk_task_id) REFERENCES task_table(id) ON DELETE CASCADE)"
//                )
//                db.execSQL("ALTER TABLE task_table ADD COLUMN date_iso8601 TEXT DEFAULT NULL")
//                db.execSQL("ALTER TABLE task_table ADD COLUMN updated_at INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP")
//
//            }
//        }
    }


}