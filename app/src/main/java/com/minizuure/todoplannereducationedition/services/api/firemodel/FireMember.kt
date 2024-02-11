package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class FireMember (
    override var reference_id: DocumentReference? = null,
    override var update_at: Timestamp? = null,
    override var created_at: Timestamp? = null
) : FireBaseModel {
    override val table: String
        get() = "Member"

    val parent: String
        get() = "${table}s"
}