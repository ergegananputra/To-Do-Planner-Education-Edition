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

    private enum class KEYS {
        USER_ID,
        USER_NAME,
        LANGUAGE,
        DEFAULT_ROUTINE_ID,
        DEFAULT_ROUTINE_NAME,
        COMMUNITY_ID,
        COMMUNITY_NAME,
        COMMUNITY_VCS,
        IS_COMMUNITY_HOST,
        FIRST_TIME,
        THEME
    }

    private object Key
    private infix fun Key.of(key: KEYS) = key.toString()


    // Getter and Setter
    var userId: String
        get() = sharedPreferences.getString(Key of KEYS.USER_ID, "")!!
        set(value) = sharedPreferences.edit().putString(Key of KEYS.USER_ID, value).apply()

    var userName: String
        get() = sharedPreferences.getString(Key of KEYS.USER_NAME, "")!!
        set(value) = sharedPreferences.edit().putString(Key of KEYS.USER_NAME, value).apply()

    var language: String
        get() = sharedPreferences.getString(Key of KEYS.LANGUAGE, "")!!
        set(value) = sharedPreferences.edit().putString(Key of KEYS.LANGUAGE, value).apply()

    var defaultRoutineId: Long
        get() = sharedPreferences.getLong(Key of KEYS.DEFAULT_ROUTINE_ID, -1L)
        set(value) = sharedPreferences.edit().putLong(Key of KEYS.DEFAULT_ROUTINE_ID, value).apply()

    var defaultRoutineName: String
        get() = sharedPreferences.getString(Key of KEYS.DEFAULT_ROUTINE_NAME, context.getString(R.string.you_have_not_set_a_default_routine))!!
        set(value) = sharedPreferences.edit().putString(Key of KEYS.DEFAULT_ROUTINE_NAME, value).apply()

    var communityId: String
        get() = sharedPreferences.getString(Key of KEYS.COMMUNITY_ID, "")!!
        set(value) = sharedPreferences.edit().putString(Key of KEYS.COMMUNITY_ID, value).apply()

    var communityName: String
        get() = sharedPreferences.getString(Key of KEYS.COMMUNITY_NAME, "")!!
        set(value) = sharedPreferences.edit().putString(Key of KEYS.COMMUNITY_NAME, value).apply()
    var communityVcs: String
        get() = sharedPreferences.getString(Key of KEYS.COMMUNITY_VCS, "")!!
        set(value) = sharedPreferences.edit().putString(Key of KEYS.COMMUNITY_VCS, value).apply()

    var isCommunityHost: Boolean
        get() = sharedPreferences.getBoolean(Key of KEYS.IS_COMMUNITY_HOST, false)
        set(value) = sharedPreferences.edit().putBoolean(Key of KEYS.IS_COMMUNITY_HOST, value).apply()

    var firstTime: Boolean
        get() = sharedPreferences.getBoolean(Key of KEYS.FIRST_TIME, true)
        set(value) = sharedPreferences.edit().putBoolean(Key of KEYS.FIRST_TIME, value).apply()

    var isThemeDark: Boolean
        get() = sharedPreferences.getBoolean(Key of KEYS.THEME, false)
        set(value) = sharedPreferences.edit().putBoolean(Key of KEYS.THEME, value).apply()


    // methods
    fun clearUser() {
        sharedPreferences.edit().apply {
            remove(Key of KEYS.USER_ID).apply()
            remove(Key of KEYS.USER_NAME).apply()
        }
    }

    fun clearCommunity() {
        sharedPreferences.edit().apply {
            remove(Key of KEYS.COMMUNITY_ID).apply()
            remove(Key of KEYS.COMMUNITY_VCS).apply()
            remove(Key of KEYS.IS_COMMUNITY_HOST).apply()
        }
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}