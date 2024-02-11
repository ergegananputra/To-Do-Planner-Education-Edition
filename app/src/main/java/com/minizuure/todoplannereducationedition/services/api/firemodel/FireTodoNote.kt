package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.minizuure.todoplannereducationedition.model.base.TodoNoteInterface
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteTable

data class FireTodoNote(
    override val id: Long = 0,
    override val fkNotesTaskId: Long = 0,
    override var isChecked: Boolean = false,
    override var description: String = "",
    override var updatedAt: String = "",
    override var reference_id: DocumentReference? = null,
    override var update_at: Timestamp? = Timestamp.now(),
    override var created_at: Timestamp? = Timestamp.now(),
) : TodoNoteInterface, FireBaseModel {

    constructor(
        todoNote: TodoNoteTable,
        reference_id: DocumentReference?,
        update_at: Timestamp?,
        created_at: Timestamp?
    ) : this(
        id = todoNote.id,
        fkNotesTaskId = todoNote.fkNotesTaskId,
        isChecked = todoNote.isChecked,
        description = todoNote.description,
        updatedAt = todoNote.updatedAt,
        reference_id = reference_id,
        update_at = update_at,
        created_at = created_at
    )

    fun convertToTable() : TodoNoteTable =
        TodoNoteTable(
            id = id,
            fkNotesTaskId = fkNotesTaskId,
            isChecked = isChecked,
            description = description,
            updatedAt = updatedAt
        )

    override val table: String
        get() = "TodoNote"

    val parent: String
        get() = "${table}s"

    object Fields {
        const val id = "id"
        const val fkNotesTaskId = "fkNotesTaskId"
        const val isChecked = "isChecked"
        const val description = "description"
        const val updatedAt = "updatedAt"
    }

    inline val firebaseMap: Map<String, Any?>
        get() = mapOf(
            Fields.id to id,
            Fields.fkNotesTaskId to fkNotesTaskId,
            Fields.isChecked to isChecked,
            Fields.description to description,
            Fields.updatedAt to updatedAt,
            FireBaseModel.Fields.reference_id to reference_id,
            FireBaseModel.Fields.update_at to update_at,
            FireBaseModel.Fields.created_at to created_at
        )

}
