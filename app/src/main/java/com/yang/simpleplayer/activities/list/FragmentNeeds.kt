package com.yang.simpleplayer.activities.list

import android.app.Application

interface FragmentNeeds {
    fun getApplication(): Application
    fun showToastMessage(msg: String)
    fun setProgressBar(visible:Boolean)
    fun setRefreshListener(update:(()->Unit))
    fun startVideoListFragment(videoIds: LongArray)
    fun setAppbarTitleText(title:String)
}