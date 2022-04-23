package com.yang.simpleplayer.activities.list

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ActivityListBinding
import com.yang.simpleplayer.fragments.list.folder.FolderListFragment
import com.yang.simpleplayer.fragments.list.playlist.PlaylistListFragment
import com.yang.simpleplayer.fragments.list.recent.RecentListFragment
import com.yang.simpleplayer.fragments.list.video.VideoListFragment
import com.yang.simpleplayer.models.Video

class ListActivity : AppCompatActivity(),FragmentNeeds {
    private var _binding: ActivityListBinding? = null
    private val binding: ActivityListBinding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        supportFragmentManager.beginTransaction().add(binding.recyclerViewContainer.id, FolderListFragment()).commit()
        /**
         * 앱바 설정
         * SearchView name 검색기능, Settings Btn
         */
        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                TODO("입력할때마다 검색 ")
                return false
            }
        })
        binding.settings.setOnClickListener {
            // TODO: Settings 버튼 클릭 리스너
        }

        binding.tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> changeRecyclerViewFragment(FolderListFragment(), false)
                    1 -> changeRecyclerViewFragment(RecentListFragment(), false)
                    2 -> changeRecyclerViewFragment(PlaylistListFragment(), false)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })

    }

    private fun changeRecyclerViewFragment(fragment: Fragment, isAddToBackStack:Boolean) {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.recyclerViewContainer.id, fragment)
            if(isAddToBackStack) addToBackStack(null)
        }.commit()
    }

    override fun showToastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    }

    override fun setProgressBar(visible:Boolean) {
        fun Boolean.toVisibility(): Int {
            return if(this) View.VISIBLE else View.GONE
        }
        binding.progressBar.visibility = visible.toVisibility()
    }

    override fun setRefreshListener(update: () -> Unit) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            update
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun startVideoListFragment(videoIds: LongArray) {
        val bundle = Bundle()
        bundle.putLongArray(R.string.videoIdsKey.toString(), videoIds)
        val fragment = VideoListFragment()
        fragment.arguments = bundle
        changeRecyclerViewFragment(fragment, true)
    }

    override fun setAppbarTitleText(title: String) { binding.appbarTitle.text = title }
}