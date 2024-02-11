package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Community(
    var owner_references : DocumentReference? = null,
    var members_references : DocumentReference? = null,

    // Tables
    var routine_references : DocumentReference? = null,

    var session_references : DocumentReference? = null,
    var task_references : DocumentReference? = null,
    var provider_references : DocumentReference? = null,

    var notes_task_references : DocumentReference? = null,
    var todo_notes_references : DocumentReference? = null,

    override var reference_id: DocumentReference? = null,
    override var update_at: Timestamp? = null,
    override var created_at: Timestamp? = null,
) : FireBaseModel {
    override val table: String
        get() = "Communities"

    object Fields {
        const val owner_references = "owner_references"
        const val members_references = "members_references"
        const val routine_references = "routine_references"
        const val session_references = "session_references"
        const val task_references = "task_references"
        const val provider_references = "provider_references"
        const val notes_task_references = "notes_task_references"
        const val todo_notes_references = "todo_notes_references"
        const val reference_id = "reference_id"
        const val update_at = "update_at"
        const val created_at = "created_at"
    }

    inline val firebaseMap: Map<String, Any?>
        get() = mapOf(
            Fields.owner_references to owner_references,
            Fields.members_references to members_references,
            Fields.routine_references to routine_references,
            Fields.session_references to session_references,
            Fields.task_references to task_references,
            Fields.provider_references to provider_references,
            Fields.notes_task_references to notes_task_references,
            Fields.todo_notes_references to todo_notes_references,
            Fields.reference_id to reference_id,
            Fields.update_at to update_at,
            Fields.created_at to created_at
        )
}
