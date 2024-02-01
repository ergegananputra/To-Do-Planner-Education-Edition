package com.minizuure.todoplannereducationedition.recycler.model

data class MainTakModel (
    val id : Long,
    var timeStart : String,
    var timeEnd : String,
    var isCommunity : Boolean,
    var indexDay : Int,

    var title : String,
    var location : String,

)