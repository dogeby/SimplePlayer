package com.yang.simpleplayer.fragments.list.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.SimplePlayerApplication
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.common.MoreDialogFactory
import com.yang.simpleplayer.databinding.FragmentPlaylistListBinding
import com.yang.simpleplayer.models.PlaylistWithVideoInfo
import com.yang.simpleplayer.viewmodels.PlaylistViewModel

class PlaylistListFragment : Fragment() {

    private var _viewModel:PlaylistViewModel? = null
    val viewModel:PlaylistViewModel get() = requireNotNull(_viewModel)
    private var _binding:FragmentPlaylistListBinding? = null
    val binding:FragmentPlaylistListBinding get() =  requireNotNull(_binding)
    private var _adapter:PlaylistListAdapter? = null
    private val adapter:PlaylistListAdapter get() = requireNotNull(_adapter)

    override fun onResume() {
        super.onResume()
        viewModel.list()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val playlistRepository = (activity?.application as SimplePlayerApplication).appContainer.playlistRepository
        _binding = FragmentPlaylistListBinding.inflate(layoutInflater)
        _viewModel = ViewModelProvider(this, PlaylistViewModel.PlaylistViewModelFactory(playlistRepository)).get(PlaylistViewModel::class.java)
        initUi()
        return binding.root
    }

    private fun initUi() {
        _adapter = PlaylistListAdapter()
        binding.playlistList.adapter = adapter
        setItemOnClickListener((activity as FragmentNeeds)::startVideoListFragment)

        setMoreBtnOnClickListener { playlistWithVideoInfo ->
            /**
             * 플레이리스트 more 버튼 클릭 시
             * 0 -> playlist 삭제
             */
            val deletePlaylist = {
                viewModel.deletePlaylist(playlistWithVideoInfo.playlist)
                viewModel.list()
            }
            context?.let { MoreDialogFactory.create(it, R.array.playlist_manage_more_btn_arr, deletePlaylist).show() }
        }

        viewModel.playlistsWithVideoInfo.observe(viewLifecycleOwner) { playlistsWithVideoInfo ->
            adapter.updatePlaylists(playlistsWithVideoInfo)
        }

        (activity as FragmentNeeds).setRefreshListener { viewModel.list() }
        (activity as FragmentNeeds).setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return false }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    fun setItemOnClickListener(itemViewCallback:(Long) -> Unit) {
        adapter.apply {
            itemViewOnClick = itemViewCallback
        }
    }

    fun setMoreBtnOnClickListener(moreBtnCallback:(PlaylistWithVideoInfo) -> Unit) {
        adapter.apply {
            moreBtnOnClick = moreBtnCallback
        }
    }
}