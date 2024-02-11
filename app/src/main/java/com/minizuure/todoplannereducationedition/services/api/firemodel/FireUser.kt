package com.minizuure.todoplannereducationedition.services.api.firemodel

data class FireUser (
    var email: String? = null,
    var username : String? = null,
    var last_updated : String? = null,
) {
    val table: String
        get() = "users"

    object Fields {
        const val email = "email"
        const val username = "username"
        const val last_updated = "last_updated"
    }

    inline val firebaseMap: Map<String, Any?>
        get() = mapOf(
            Fields.email to email,
            Fields.username to username,
            Fields.last_updated to last_updated
        )
}