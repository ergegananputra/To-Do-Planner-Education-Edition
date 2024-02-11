package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.minizuure.todoplannereducationedition.model.base.NotesTaskInterface
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskTable

data class FireNotesTask(
    override val id: Long = 0,
    override val fkTaskId: Long = 0,
    override var dateISO8601: String = "",
    override var category: String = "",
    override var description: String = "",
    override var updatedAt: String = "",
    override var reference_id: DocumentReference? = null,
    override var update_at: Timestamp? = Timestamp.now(),
    override var created_at: Timestamp? = Timestamp.now(),
) : NotesTaskInterface, FireBaseModel {

    constructor(
        notesTask: NotesTaskTable,
        reference_id: DocumentReference?,
        update_at: Timestamp?,
        created_at: Timestamp?
    ) : this(
        id = notesTask.id,
        fkTaskId = notesTask.fkTaskId,
        dateISO8601 = notesTask.dateISO8601,
        category = notesTask.category,
        description = notesTask.description,
        updatedAt = notesTask.updatedAt,
        reference_id = reference_id,
        update_at = update_at,
        created_at = created_at
    )

    fun convertToTable() : NotesTaskTable =
        NotesTaskTable(
            id = id,
            fkTaskId = fkTaskId,
            dateISO8601 = dateISO8601,
            category = category,
            description = description,
            updatedAt = updatedAt
        )

    override val table: String
        get() = "NotesTask"

    val parent: String
        get() = "${table}s"

    object Fields {
        const val id = "id"
        const val fkTaskId = "fkTaskId"
        const val dateISO8601 = "dateISO8601"
        const val category = "category"
        const val description = "description"
        const val updatedAt = "updatedAt"
    }

    inline val firebaseMap: Map<String, Any?>
        get() = mapOf(
            Fields.id to id,
            Fields.fkTaskId to fkTaskId,
            Fields.dateISO8601 to dateISO8601,
            Fields.category to category,
            Fields.description to description,
            Fields.updatedAt to updatedAt,
            FireBaseModel.Fields.reference_id to reference_id,
            FireBaseModel.Fields.update_at to update_at,
            FireBaseModel.Fields.created_at to created_at
        )


}
