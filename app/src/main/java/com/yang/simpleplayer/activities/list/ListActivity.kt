package com.yang.simpleplayer.activities.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ActivityListBinding
import com.yang.simpleplayer.fragments.list.folder.FolderListFragment
import com.yang.simpleplayer.fragments.list.playlist.PlaylistListFragment
import com.yang.simpleplayer.fragments.list.recent.RecentListFragment
import com.yang.simpleplayer.fragments.list.video.VideoListFragment

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
        binding.settings.setOnClickListener {
            // TODO: Settings 버튼 클릭 리스너
        }
        binding.searchBar.setOnSearchClickListener { binding.appbarTitle.visibility = View.GONE }
        binding.searchBar.setOnCloseListener { binding.appbarTitle.visibility = View.VISIBLE; false }

        binding.tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            val listener = {position:Int ->
                when(position) {
                    0 -> changeRecyclerViewFragment(FolderListFragment(), false)
                    1 -> changeRecyclerViewFragment(RecentListFragment(), false)
                    2 -> changeRecyclerViewFragment(PlaylistListFragment(), false)
                }
            }
            override fun onTabSelected(tab: TabLayout.Tab?) { tab?.position?.let { listener(it) }}
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                clearBackStack()
                tab?.position?.let { listener(it) }}
        })
    }

    private fun clearBackStack() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun changeRecyclerViewFragment(fragment: Fragment, isAddToBackStack:Boolean) {
        setSearchBarIconified()
        supportFragmentManager.beginTransaction().apply {
            replace(binding.recyclerViewContainer.id, fragment)
            if(isAddToBackStack) addToBackStack(null)
        }.commit()
    }

    private fun setSearchBarIconified() {
        binding.searchBar.isIconified = true
        binding.searchBar.isIconified = true
    }

    override fun onBackPressed() {
        if(!binding.searchBar.isIconified) {
            setSearchBarIconified()
        }
        else super.onBackPressed()
    }

    override fun showToastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun setProgressBar(visible:Boolean) {
        binding.progressBar.visibility = if(visible) View.VISIBLE else View.GONE
    }

    override fun setRefreshListener(update: () -> Unit) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            update
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun startVideoListFragment(folderName: String) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.folderNameKey), folderName)
        val fragment = VideoListFragment()
        fragment.arguments = bundle
        changeRecyclerViewFragment(fragment, true)
    }

    override fun setAppbarTitleText(title: String) { binding.appbarTitle.text = title }

    override fun setOnQueryTextListener(listener: SearchView.OnQueryTextListener) { binding.searchBar.setOnQueryTextListener(listener) }
}