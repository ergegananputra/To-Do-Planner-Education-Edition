package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.minizuure.todoplannereducationedition.model.base.RoutineInterface
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable

data class FireRoutine(
    override val id: Long = 0,
    override var title: String = "",
    override var description: String = "",
    override var date_start: String = "",
    override var date_end: String = "",
    override var isSharedToCommunity: Boolean = false,
    override var communityId: String? = null,
    override var reference_id: DocumentReference? = null,
    override var update_at: Timestamp? = Timestamp.now(),
    override var created_at: Timestamp? = Timestamp.now(),
) : RoutineInterface, FireBaseModel {

    constructor(
        routineTable: RoutineTable,
        reference_id: DocumentReference?,
        update_at: Timestamp?,
        created_at: Timestamp?
    ) : this(
        id = routineTable.id,
        title = routineTable.title,
        description = routineTable.description,
        date_start = routineTable.date_start,
        date_end = routineTable.date_end,
        isSharedToCommunity = routineTable.isSharedToCommunity,
        communityId = routineTable.communityId,
        reference_id = reference_id,
        update_at = update_at,
        created_at = created_at
    )

    fun convertToTable() : RoutineTable =
         RoutineTable(
            id = id,
            title = title,
            description = description,
            date_start = date_start,
            date_end = date_end,
            isSharedToCommunity = isSharedToCommunity,
            communityId = communityId
        )

    override val table: String
        get() = "Routine"

    object Fields {
        const val id = "id"
        const val title = "title"
        const val description = "description"
        const val date_start = "date_start"
        const val date_end = "date_end"
        const val isSharedToCommunity = "isSharedToCommunity"
        const val communityId = "communityId"
    }

    inline val firebaseMap: Map<String, Any?>
        get() = mapOf(
            Fields.id to id,
            Fields.title to title,
            Fields.description to description,
            Fields.date_start to date_start,
            Fields.date_end to date_end,
            Fields.isSharedToCommunity to isSharedToCommunity,
            Fields.communityId to communityId,
            FireBaseModel.Fields.reference_id to reference_id,
            FireBaseModel.Fields.update_at to update_at,
            FireBaseModel.Fields.created_at to created_at
        )



}
