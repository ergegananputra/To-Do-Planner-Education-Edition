package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.minizuure.todoplannereducationedition.model.base.SessionTaskProviderInterface
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderTable

data class FireSessionTaskProvider(
    override val indexDay: Int = 0,
    override val fkTaskId: Long = 0,
    override val fkSessionId: Long = 0,
    override var isRescheduled: Boolean = false,
    override var rescheduledDateStart: String? = null,
    override var rescheduledDateEnd: String? = null,
    override var locationName: String? = null,
    override var locationLink: String? = null,
    override var reference_id: DocumentReference? = null,
    override var update_at: Timestamp? = null,
    override var created_at: Timestamp? = null,
) : SessionTaskProviderInterface, FireBaseModel {

    constructor(
        sessionTaskProvider: SessionTaskProviderTable,
        reference_id: DocumentReference?,
        update_at: Timestamp?,
        created_at: Timestamp?
    ) : this(
        indexDay = sessionTaskProvider.indexDay,
        fkTaskId = sessionTaskProvider.fkTaskId,
        fkSessionId = sessionTaskProvider.fkSessionId,
        isRescheduled = sessionTaskProvider.isRescheduled,
        rescheduledDateStart = sessionTaskProvider.rescheduledDateStart,
        rescheduledDateEnd = sessionTaskProvider.rescheduledDateEnd,
        locationName = sessionTaskProvider.locationName,
        locationLink = sessionTaskProvider.locationLink,
        reference_id = reference_id,
        update_at = update_at,
        created_at = created_at
    )

    fun convertToTable() : SessionTaskProviderTable =
        SessionTaskProviderTable(
            indexDay = indexDay,
            fkTaskId = fkTaskId,
            fkSessionId = fkSessionId,
            isRescheduled = isRescheduled,
            rescheduledDateStart = rescheduledDateStart,
            rescheduledDateEnd = rescheduledDateEnd,
            locationName = locationName,
            locationLink = locationLink
        )

    override val table: String
        get() = "SessionTaskProvider"

    val parent: String
        get() = "${table}s"

    object Fields {
        const val indexDay = "indexDay"
        const val fkTaskId = "fkTaskId"
        const val fkSessionId = "fkSessionId"
        const val isRescheduled = "isRescheduled"
        const val rescheduledDateStart = "rescheduledDateStart"
        const val rescheduledDateEnd = "rescheduledDateEnd"
        const val locationName = "locationName"
        const val locationLink = "locationLink"
    }

    inline val firebaseMap: Map<String, Any?>
        get() = mapOf(
            Fields.indexDay to indexDay,
            Fields.fkTaskId to fkTaskId,
            Fields.fkSessionId to fkSessionId,
            Fields.isRescheduled to isRescheduled,
            Fields.rescheduledDateStart to rescheduledDateStart,
            Fields.rescheduledDateEnd to rescheduledDateEnd,
            Fields.locationName to locationName,
            Fields.locationLink to locationLink,
            FireBaseModel.Fields.reference_id to reference_id,
            FireBaseModel.Fields.update_at to update_at,
            FireBaseModel.Fields.created_at to created_at
        )

}
