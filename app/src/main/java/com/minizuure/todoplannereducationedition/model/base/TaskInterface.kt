package com.minizuure.todoplannereducationedition.model.base

interface TaskInterface : GlobalMinimumInterface {
    var updatedAt: String
    var isSharedToCommunity : Boolean
    var communityId : String?
}