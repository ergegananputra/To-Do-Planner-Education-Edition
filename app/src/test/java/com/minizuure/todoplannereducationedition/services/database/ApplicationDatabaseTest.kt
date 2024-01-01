package com.minizuure.todoplannereducationedition.services.database

import junit.framework.TestCase
import org.junit.jupiter.api.Assertions.*
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTableDao
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionTableDao
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskTableDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

/**
 *  This test class is based on the following tutorial:
 *  https://www.geeksforgeeks.org/testing-room-database-in-android-using-junit/
 *
 *  status : Not Working
 *  TODO: Fix this test case
 */
class ApplicationDatabaseTest : TestCase() {
    private lateinit var db: ApplicationDatabase

    private lateinit var routineTableDao: RoutineTableDao
    private lateinit var sessionTableDao: SessionTableDao
    private lateinit var taskTableDao: TaskTableDao
    private lateinit var deleteAllOperation: DeleteAllOperation


    @Before
    override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ApplicationDatabase::class.java).build()

        routineTableDao = db.routineTableDao()
        sessionTableDao = db.sessionTableDao()
        taskTableDao = db.taskTableDao()
        deleteAllOperation = db.deleteAllOperation()

    }



    @After
    fun close() = runBlocking{
        deleteAllOperation.deleteAllTasks()
        db.close()
    }

    @Test
    fun basicDatabaseInputTest() = runBlocking {
        val routineTable = RoutineTable(
            1,
            "Unit Test Case 1",
            "This is unit test case to test the database",
            "09/12/2023",
            "09/12/2024"
        )
        routineTableDao.insert(routineTable)
        val routineTableFromDatabase = routineTableDao.getAll()
        assertThat(routineTableFromDatabase.contains(routineTable).toString(), true)


        val sessionTable = SessionTable(
            1,
            "Session - Unit Test Case 1",
            "08:15",
            "09:15",
            "09/12/2024",
            1
        )
        sessionTableDao.insert(sessionTable)
        val sessionTableFromDatabase = sessionTableDao.getAll()
        assertThat(sessionTableFromDatabase.contains(sessionTable).toString(), true)

        val sesionForRoutine = sessionTableDao.getSessionsForRoutine(1)
        assertThat(sesionForRoutine.contains(sessionTable).toString(), true)


        val taskTable = TaskTable(
            1,
            "Task - Unit Test Case 1",
            3,
            1,
            false,
        )
        taskTableDao.insert(taskTable)
        val taskTableDb = taskTableDao.getBySessionId(1)
        assertThat(taskTableDb.contains(taskTable).toString(), true)


    }

}