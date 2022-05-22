package com.yang.simpleplayer.activities.list

import androidx.appcompat.widget.SearchView

interface FragmentNeeds {
    fun showToastMessage(resId:Int)
    fun setRefreshListener(update:()->Unit)
    fun startVideoListFragment(folderName: String)
    fun startVideoListFragment(playlistId:Long)
    fun startPlayerActivity(currentVideoId:Long, videoIds: LongArray)
    fun startPlaylistManageActivity(videoIds:LongArray)
    fun setOnQueryTextListener(listener: SearchView.OnQueryTextListener)
}