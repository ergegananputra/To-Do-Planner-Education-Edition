package com.minizuure.todoplannereducationedition.model

import com.google.firebase.Timestamp

data class Users(
    var email : String = "",
    var username : String = "",
    var last_updated : Timestamp = Timestamp.now(),
) {
    val table : String
        get() = "users"

    fun convertToMapFirebase() =
        hashMapOf(
            "email" to email,
            "username" to username,
            "last_updated" to last_updated
        )

}
