package com.yang.simpleplayer.fragments.list.playlist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.SimplePlayerApplication
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.common.MoreDialogFactory
import com.yang.simpleplayer.databinding.DialogPlaylistNameBinding
import com.yang.simpleplayer.databinding.FragmentPlaylistListBinding
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistWithVideoInfo
import com.yang.simpleplayer.viewmodels.PlaylistViewModel

class PlaylistListFragment : Fragment() {

    private var _viewModel:PlaylistViewModel? = null
    val viewModel:PlaylistViewModel get() = requireNotNull(_viewModel)
    private var _binding:FragmentPlaylistListBinding? = null
    val binding:FragmentPlaylistListBinding get() =  requireNotNull(_binding)
    private var _adapter:PlaylistListAdapter? = null
    private val adapter:PlaylistListAdapter get() = requireNotNull(_adapter)
    private val emptyView:TextView by lazy {
        (layoutInflater.inflate(R.layout.view_empty_list, null) as TextView).apply {
            setText(R.string.empty_playlistList)
        }
    }

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

        val editNameAlertDialog = { title:String, positiveBtnListener:(String)->Unit -> //플레이리스트 이름 관련 AlertDialog
            val builder = context?.let { AlertDialog.Builder(it).apply { setTitle(title) } }
            val dialogAddPlaylistBinding = DialogPlaylistNameBinding.inflate(layoutInflater)
            builder?.setView(dialogAddPlaylistBinding.root)
                ?.setPositiveButton(R.string.add) {_, _ -> positiveBtnListener(dialogAddPlaylistBinding.playlistNameEt.text.toString())}
                ?.setNegativeButton(R.string.cancel){_, _ ->}
            val dialog = builder?.create()?.apply { show() }
            dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
            dialogAddPlaylistBinding.playlistNameEt.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if(s?.isBlank() == true) {
                        dialogAddPlaylistBinding.playlistNameTextInputLayout.error = getString(R.string.blank_playlist_name_exception)
                        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
                    } else {
                        viewModel.isSameNamePlaylist(Playlist(s.toString())){ isSameNamePlaylist ->
                            if(isSameNamePlaylist) {
                                dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
                                dialogAddPlaylistBinding.playlistNameTextInputLayout.error = getString(R.string.name_duplicate_exception)
                            } else {
                                dialogAddPlaylistBinding.playlistNameTextInputLayout.error = ""
                                dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
                            }
                        }
                    }
                }
            })
        }
        binding.addPlaylist.setOnClickListener{editNameAlertDialog(getString(R.string.add_playlist)) {inputPlaylistName ->
            viewModel.insertPlaylist(Playlist(inputPlaylistName))} }

        setItemOnClickListener((activity as FragmentNeeds)::startVideoListFragment)

        setMoreBtnOnClickListener { playlistWithVideoInfo ->
            /**
             * 플레이리스트 more 버튼 클릭 시
             * playlist 이름 변경
             * playlist 삭제
             */
            val callbacks = mutableListOf<()->Unit>()
            callbacks.add{  //플레이리스트 수정
                editNameAlertDialog(resources.getStringArray(R.array.playlist_manage_more_btn_arr)[0]) {inputPlaylistName ->
                    viewModel.updatePlaylist(Playlist(playlistWithVideoInfo.playlist.playlistId, inputPlaylistName))
                }
            }
            callbacks.add { //플레이리스트 삭제
                viewModel.deletePlaylist(playlistWithVideoInfo.playlist)
                viewModel.list()
            }
            context?.let { MoreDialogFactory.create(it, R.array.playlist_manage_more_btn_arr, *callbacks.toTypedArray()).show() }
        }

        val rootViewSize = binding.root.size
        viewModel.playlistsWithVideoInfo.observe(viewLifecycleOwner) { playlistsWithVideoInfo ->
            if(playlistsWithVideoInfo.isNotEmpty()) {
                if(binding.root.size > rootViewSize) binding.root.removeView(emptyView)
                adapter.updatePlaylists(playlistsWithVideoInfo)

            } else if(binding.root.size == rootViewSize) {  //리스트가 비어있는 경우
                binding.root.addView(emptyView)
            }
            adapter.updatePlaylists(playlistsWithVideoInfo)
        }
        viewModel.doListUpdate.observe(viewLifecycleOwner) { doListUpdate ->
            if(doListUpdate) viewModel.list()
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