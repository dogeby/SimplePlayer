package com.yang.simpleplayer.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.databinding.ActivityPlaylistManageBinding
import com.yang.simpleplayer.fragments.list.playlist.PlaylistListFragment
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef

class PlaylistManageActivity : AppCompatActivity(),FragmentNeeds{

    private var _binding: ActivityPlaylistManageBinding? = null
    private val binding: ActivityPlaylistManageBinding get() = requireNotNull(_binding)
    private var isDefault = true //ListActivity recreate시 listFragment 중복 생성 문제 방지
    private val videoIdsKey = "videoIds"
    private lateinit var videoIds:LongArray
    private val playlistListFragment by lazy {
        PlaylistListFragment()
    }
    private val isDefaultKey = "isDefaultKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState != null) {
            isDefault = savedInstanceState.getBoolean(isDefaultKey)
        }
        _binding = ActivityPlaylistManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        if(isDefault) {
            supportFragmentManager.beginTransaction()
                .add(binding.recyclerViewContainer.id, playlistListFragment).commit()
        }
        videoIds = intent.getLongArrayExtra(videoIdsKey) ?: LongArray(0)
    }

    override fun onStart() {
        super.onStart()
        playlistListFragment.setItemOnClickListener { playlistId ->
            videoIds?.forEach { videoId ->
                playlistListFragment.viewModel.addVideoInfoOnPlaylist(PlaylistVideoInfoCrossRef(playlistId,videoId))
                finish()
            }
        }
        playlistListFragment.setMoreBtnOnClickListener { playlistWithVideoInfo ->
            this.let {
                val builder = AlertDialog.Builder(it)
                builder.setItems(R.array.playlist_manage_more_btn_arr, DialogInterface.OnClickListener{ dialog, which ->
                    when(which) {
                        /**
                         * 0 -> 플레이리스트 삭제
                         */
                        0 -> {
                            playlistListFragment.viewModel.deletePlaylist(playlistWithVideoInfo.playlist)
                            playlistListFragment.viewModel.list()
                        }
                    }
                }).create().show()
            }
        }
        binding.addPlaylist.setOnClickListener {
            startPlaylistAddActivity()
        }
    }

    private fun startPlaylistAddActivity() {
        isDefault = false
        Intent(this, PlaylistInsertActivity::class.java).run { startActivity(this) }
    }

    override fun showToastMessage(msg: String) { }
    override fun setProgressBar(visible: Boolean) { }
    override fun setRefreshListener(update: () -> Unit) {
        binding.swipeRefreshLayout.setOnRefreshListener{
            update()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
    override fun startVideoListFragment(folderName: String) { }
    override fun startVideoListFragment(playlistId: Long) { }
    override fun startPlayerActivity(currentVideoId: Long, videoIds: LongArray) { }
    override fun startPlaylistManageActivity(videoIds: LongArray) { }
    override fun setOnQueryTextListener(listener: SearchView.OnQueryTextListener) { }
}