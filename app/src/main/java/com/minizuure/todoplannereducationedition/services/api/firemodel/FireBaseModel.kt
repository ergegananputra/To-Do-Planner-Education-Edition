package com.minizuure.todoplannereducationedition.services.api.firemodel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

interface FireBaseModel {
    var reference_id : DocumentReference?
    var update_at : Timestamp?
    var created_at : Timestamp?

    val table : String

    object Fields {
        const val reference_id = "reference_id"
        const val update_at = "update_at"
        const val created_at = "created_at"
    }
}
