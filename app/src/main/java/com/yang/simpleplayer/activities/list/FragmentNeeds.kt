package com.yang.simpleplayer.activities.list

import android.app.Application
import androidx.appcompat.widget.SearchView

interface FragmentNeeds {
    fun getApplication(): Application
    fun showToastMessage(resId:Int)
    fun setProgressBar(visible:Boolean)
    fun setRefreshListener(update:()->Unit)
    fun startVideoListFragment(folderName: String)
    fun startVideoListFragment(playlistId:Long)
    fun startPlayerActivity(currentVideoId:Long, videoIds: LongArray)
    fun startPlaylistManageActivity(videoIds:LongArray)
    fun setOnQueryTextListener(listener: SearchView.OnQueryTextListener)
}