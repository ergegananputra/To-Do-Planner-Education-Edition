package com.minizuure.todoplannereducationedition.services.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.minizuure.todoplannereducationedition.R

class UserPreferences(private val context: Context) {
    private val PREFERENCES_NAME = "personal_user_data"
    private val mKA = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        PREFERENCES_NAME,
        mKA,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Pref ID
    private val KEY_USER_ID = "user_id"
    private val KEY_USER_NAME = "user_name"

    private val KEY_LANGUAGE = "application_language"
    private val KEY_DEFAULT_ROUTINE_ID = "default_routine_id"
    private val KEY_DEFAULT_ROUTINE_NAME = "default_routine_name"

    private val KEY_COMMUNITY_ID = "community_id"
    private val KEY_COMMUNITY_VCS = "community_vcs" // update version code and version name

    private val KEY_FIRST_TIME = "first_time"


    // Getter and Setter
    var userId: String
        get() = sharedPreferences.getString(KEY_USER_ID, "")!!
        set(value) = sharedPreferences.edit().putString(KEY_USER_ID, value).apply()

    var userName: String
        get() = sharedPreferences.getString(KEY_USER_NAME, "")!!
        set(value) = sharedPreferences.edit().putString(KEY_USER_NAME, value).apply()

    var language: String
        get() = sharedPreferences.getString(KEY_LANGUAGE, "")!!
        set(value) = sharedPreferences.edit().putString(KEY_LANGUAGE, value).apply()

    var defaultRoutineId: Long
        get() = sharedPreferences.getLong(KEY_DEFAULT_ROUTINE_ID, -1L)
        set(value) = sharedPreferences.edit().putLong(KEY_DEFAULT_ROUTINE_ID, value).apply()

    var defaultRoutineName: String
        get() = sharedPreferences.getString(KEY_DEFAULT_ROUTINE_NAME, context.getString(R.string.you_have_not_set_a_default_routine))!!
        set(value) = sharedPreferences.edit().putString(KEY_DEFAULT_ROUTINE_NAME, value).apply()

    var communityId: String
        get() = sharedPreferences.getString(KEY_COMMUNITY_ID, "")!!
        set(value) = sharedPreferences.edit().putString(KEY_COMMUNITY_ID, value).apply()

    var communityVcs: String
        get() = sharedPreferences.getString(KEY_COMMUNITY_VCS, "")!!
        set(value) = sharedPreferences.edit().putString(KEY_COMMUNITY_VCS, value).apply()

    var firstTime: Boolean
        get() = sharedPreferences.getBoolean(KEY_FIRST_TIME, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_FIRST_TIME, value).apply()


    // methods
    fun clearUser() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_ID).apply()
            remove(KEY_USER_NAME).apply()
        }
    }

    fun clearCommunity() {
        sharedPreferences.edit().apply {
            remove(KEY_COMMUNITY_ID).apply()
            remove(KEY_COMMUNITY_VCS).apply()
        }
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}