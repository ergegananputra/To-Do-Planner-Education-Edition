package com.minizuure.todoplannereducationedition.services.database.join

import androidx.room.ColumnInfo

data class TaskAndSessionJoin(
    // Task
    @ColumnInfo(name ="id")
    val id : Long,

    @ColumnInfo(name ="title")
    val title : String,

    @ColumnInfo(name ="index_day")
    val indexDay : Int,

    @ColumnInfo(name ="session_id")
    val sessionId : Long,

    @ColumnInfo(name ="updated_at")
    val updatedAt : String,

    @ColumnInfo(name ="location_name")
    val locationName : String?,

    @ColumnInfo(name ="location_link")
    val locationAddress : String?,

    @ColumnInfo(name ="is_shared_to_community")
    val isSharedToCommunity : Boolean,

    @ColumnInfo(name ="community_id")
    val communityId : Long,

    // Session
    @ColumnInfo(name ="session_title")
    val sessionTitle : String,

    @ColumnInfo(name ="time_start")
    val sessionTimeStart : String,

    @ColumnInfo(name ="time_end")
    val sessionTimeEnd : String,

    @ColumnInfo(name ="bool_selected_days")
    val sessionSelectedDays : String,

    @ColumnInfo(name ="fk_routine_id")
    val sessionFkRoutineId : Long,

    @ColumnInfo(name ="is_custom_session")
    val sessionIsCustomSession : Boolean,


    // custom reschedule
    @ColumnInfo(name = "is_rescheduled")
    var isRescheduled : Boolean = false,

    @ColumnInfo(name = "rescheduled_time_start")
    var rescheduledTimeStart : String? = null,

    @ColumnInfo(name = "rescheduled_time_end")
    var rescheduledTimeEnd : String? = null,

)
