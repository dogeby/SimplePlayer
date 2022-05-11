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
import com.yang.simpleplayer.databinding.FragmentPlaylistListBinding
import com.yang.simpleplayer.viewmodels.PlaylistViewModel

class PlaylistListFragment : Fragment() {

    private var _viewModel:PlaylistViewModel? = null
    private val viewModel:PlaylistViewModel get() = requireNotNull(_viewModel)
    private var _binding:FragmentPlaylistListBinding? = null
    private val binding:FragmentPlaylistListBinding get() =  requireNotNull(_binding)


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
        val adapter = PlaylistListAdapter().apply {
            itemViewOnClick = (activity as FragmentNeeds)::startVideoListFragment
            // TODO: more 버튼 리스너 작성
        }
        binding.playlistList.adapter = adapter

        viewModel.playlistsWithVideoInfo.observe(viewLifecycleOwner) { playlistsWithVideoInfo ->
            adapter.updatePlaylists(playlistsWithVideoInfo)
        }

        viewModel.playlistList.observe(viewLifecycleOwner) { playlists ->
            // TODO: 플레이리스트 목록 불러오기
        }

        (activity as FragmentNeeds).setRefreshListener { viewModel.list() }
        (activity as FragmentNeeds).setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return false }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        (activity as FragmentNeeds).setAppbarTitleText(getString(R.string.appbar_title_playlist))
        viewModel.list()
    }
}