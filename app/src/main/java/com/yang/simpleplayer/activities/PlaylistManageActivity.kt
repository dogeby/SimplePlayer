package com.yang.simpleplayer.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.databinding.ActivityPlaylistManageBinding
import com.yang.simpleplayer.databinding.DialogPlaylistNameBinding
import com.yang.simpleplayer.fragments.list.playlist.PlaylistListFragment
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef

class PlaylistManageActivity : AppCompatActivity(),FragmentNeeds{

    private var _binding: ActivityPlaylistManageBinding? = null
    private val binding: ActivityPlaylistManageBinding get() = requireNotNull(_binding)
    private var isDefault = true //PlaylistManageActivity recreate시 PlaylistFragment 중복 생성 문제 방지
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
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        playlistListFragment.setItemOnClickListener { playlistId -> //동영상들을 클릭한 플레이 리스트에 추가
            videoIds.forEach { videoId ->
                playlistListFragment.viewModel.addVideoInfoOnPlaylist(PlaylistVideoInfoCrossRef(playlistId,videoId))
                finish()
            }
        }
        binding.addPlaylist.setOnClickListener {    //플레이리스트 추가 버튼 클릭 시 다이어그램 보여주기
                val builder = AlertDialog.Builder(this)
                val dialogAddPlaylistBinding = DialogPlaylistNameBinding.inflate(layoutInflater)
                builder.setView(dialogAddPlaylistBinding.root)
                    .setPositiveButton(R.string.ok){ _, _ ->
                        val inputPlaylistName = dialogAddPlaylistBinding.playlistNameEt.text.toString()
                        playlistListFragment.viewModel.insertPlaylist(Playlist(inputPlaylistName))
                    }
                    .setNegativeButton(R.string.cancel){_, _ ->}
                builder.show()
            }
    }

    override fun showToastMessage(resId:Int) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
    }
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