package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.minizuure.todoplannereducationedition.model.base.TaskInterface
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable

data class FireTask(
    override val id: Long = 0,
    override var title: String = "",
    override var updatedAt: String = "",
    override var isSharedToCommunity: Boolean = false,
    override var communityId: String? = null,
    override var reference_id: DocumentReference? = null,
    override var update_at: Timestamp? = Timestamp.now(),
    override var created_at: Timestamp? = Timestamp.now(),
) : TaskInterface, FireBaseModel {

    constructor(
        taskTable: TaskTable,
        reference_id: DocumentReference?,
        update_at: Timestamp?,
        created_at: Timestamp?
    ) : this(
        id = taskTable.id,
        title = taskTable.title,
        updatedAt = taskTable.updatedAt,
        isSharedToCommunity = taskTable.isSharedToCommunity,
        communityId = taskTable.communityId,
        reference_id = reference_id,
        update_at = update_at,
        created_at = created_at
    )

    fun convertToTable() : TaskTable =
         TaskTable(
            id = id,
            title = title,
            updatedAt = updatedAt,
            isSharedToCommunity = isSharedToCommunity,
            communityId = communityId
        )

    override val table: String
        get() = "Task"

    val parent: String
        get() = "${table}s"

    object Fields {
        const val id = "id"
        const val title = "title"
        const val updatedAt = "updatedAt"
        const val isSharedToCommunity = "isSharedToCommunity"
        const val communityId = "communityId"
    }

    inline val firebaseMap: Map<String, Any?>
        get() = mapOf(
            Fields.id to id,
            Fields.title to title,
            Fields.updatedAt to updatedAt,
            Fields.isSharedToCommunity to isSharedToCommunity,
            Fields.communityId to communityId,
            FireBaseModel.Fields.reference_id to reference_id,
            FireBaseModel.Fields.update_at to update_at,
            FireBaseModel.Fields.created_at to created_at
        )


}
