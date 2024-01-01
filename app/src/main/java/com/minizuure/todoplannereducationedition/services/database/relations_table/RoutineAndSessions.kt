package com.minizuure.todoplannereducationedition.services.database.relations_table

import androidx.room.Embedded
import androidx.room.Relation
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable

data class RoutineAndSessions(
    @Embedded val routine: RoutineTable,
    @Relation(
        parentColumn = "id",
        entityColumn = "fk_routine_id"
    )
    val sessions: List<SessionTable>
)
