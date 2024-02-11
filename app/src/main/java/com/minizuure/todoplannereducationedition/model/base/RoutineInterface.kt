package com.minizuure.todoplannereducationedition.model.base

interface RoutineInterface : GlobalMinimumInterface {
    var description: String
    var date_start: String
    var date_end: String
    var isSharedToCommunity: Boolean
    var communityId: String?
}