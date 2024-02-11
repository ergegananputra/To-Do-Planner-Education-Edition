package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.minizuure.todoplannereducationedition.model.base.SessionInterface
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable

data class FireSession(
    override val id: Long = 0,
    override var title: String = "",
    override var timeStart: String = "",
    override var timeEnd: String = "",
    override var selectedDays: String = "",
    override val fkRoutineId: Long = 0,
    override var isCustomSession: Boolean = false,
    override var reference_id: DocumentReference? = null,
    override var update_at: Timestamp? = Timestamp.now(),
    override var created_at: Timestamp? = Timestamp.now(),
) : SessionInterface, FireBaseModel {

    constructor(
        sessionTable: SessionTable,
        reference_id: DocumentReference?,
        update_at: Timestamp?,
        created_at: Timestamp?
    ) : this(
        id = sessionTable.id,
        title = sessionTable.title,
        timeStart = sessionTable.timeStart,
        timeEnd = sessionTable.timeEnd,
        selectedDays = sessionTable.selectedDays,
        fkRoutineId = sessionTable.fkRoutineId,
        isCustomSession = sessionTable.isCustomSession,
        reference_id = reference_id,
        update_at = update_at,
        created_at = created_at
    )

    fun convertToTable() : SessionTable =
        SessionTable(
            id = id,
            title = title,
            timeStart = timeStart,
            timeEnd = timeEnd,
            selectedDays = selectedDays,
            fkRoutineId = fkRoutineId,
            isCustomSession = isCustomSession
        )

    override val table: String
        get() = "Session"

    val parent: String
        get() = "${table}s"

    object Fields {
        const val id = "id"
        const val title = "title"
        const val timeStart = "timeStart"
        const val timeEnd = "timeEnd"
        const val selectedDays = "selectedDays"
        const val fkRoutineId = "fkRoutineId"
        const val isCustomSession = "isCustomSession"
    }

    inline val firebaseMap: Map<String, Any?>
        get() = mapOf(
            Fields.id to id,
            Fields.title to title,
            Fields.timeStart to timeStart,
            Fields.timeEnd to timeEnd,
            Fields.selectedDays to selectedDays,
            Fields.fkRoutineId to fkRoutineId,
            Fields.isCustomSession to isCustomSession,
            FireBaseModel.Fields.reference_id to reference_id,
            FireBaseModel.Fields.update_at to update_at,
            FireBaseModel.Fields.created_at to created_at
        )
}
