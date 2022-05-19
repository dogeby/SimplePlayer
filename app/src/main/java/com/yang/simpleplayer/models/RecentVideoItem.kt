package com.yang.simpleplayer.models

data class RecentVideoItem(
    val video:Video? = null,
    val date: String? = null,
    var viewType:Int = VIDEO
) {
    companion object {
        const val VIDEO = 0
        const val DATE = 1
    }
}