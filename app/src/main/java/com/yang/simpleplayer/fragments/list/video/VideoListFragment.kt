package com.yang.simpleplayer.fragments.list.video

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
import com.yang.simpleplayer.databinding.FragmentVideoListBinding
import com.yang.simpleplayer.viewmodels.VideoListViewModel

class VideoListFragment : Fragment() {

    private var _binding: FragmentVideoListBinding? = null
    private val binding: FragmentVideoListBinding get() = requireNotNull(_binding)
    private var _viewModel: VideoListViewModel? = null
    private val viewModel: VideoListViewModel get() = requireNotNull(_viewModel)
    private var _source: Any? = null
    private val source: Any get() = requireNotNull(_source)
    private val folderNameKey = "folderName"
    private val playlistIdKey = "playlistId"

    // TODO: source any인거 바꾸기
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val videoRepo = (activity?.application as SimplePlayerApplication).appContainer.videoRepository
        val playlistRepo = (activity?.application as SimplePlayerApplication).appContainer.playlistRepository
        _binding = FragmentVideoListBinding.inflate(layoutInflater)
        arguments?.let {
            _source = it.getString(folderNameKey)?:it.getLong(playlistIdKey)
        }
        _viewModel = ViewModelProvider(this, VideoListViewModel.VideoListViewModelFactory(videoRepo, playlistRepo)).get(VideoListViewModel::class.java)
        initUi()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // TODO: RecyclerView 최적화 필요
        viewModel.list(source)
    }

    private fun initUi() {
        val adapter = VideoListAdapter().apply {
            itemViewOnclick = (activity as FragmentNeeds)::startPlayerActivity
            moreBtnOnClick = { video ->
                /**
                 * video more 버튼 클릭 시
                 * 플레이리스트에 동영상 추가
                 * 만약 플레이리스트의 동영상 more 버튼을 클릭한거면 플레이리스트에서 해당 동영상 제거
                 */
                val moreBtnStrArr = mutableListOf<String>()
                val callbacks = mutableListOf<()->Unit>()
                moreBtnStrArr.add(getString(R.string.videoAddToPlaylist))
                callbacks.add {(activity as FragmentNeeds).startPlaylistManageActivity(longArrayOf(video.id))}
                if(source is Long) {
                    moreBtnStrArr.add(getString(R.string.videoDeleteFromPlaylist))
                    callbacks.add {
                        viewModel.deleteVideoFromPlaylist(video.id, source as Long)
                        viewModel.list(source)
                    }
                }
                context?.let { MoreDialogFactory.create(it, moreBtnStrArr.toTypedArray(), *callbacks.toTypedArray()).show() }
            }
        }
        binding.videoList.adapter = adapter

        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            adapter.updateVideos(videos)
        }
//        viewModel.progressVisible.observe(viewLifecycleOwner) { progressVisible ->
//            (activity as FragmentNeeds).setProgressBar(progressVisible)
//        }
        viewModel.exceptionMessageResId.observe(viewLifecycleOwner) { exceptionMessageResId ->
            (activity as FragmentNeeds).showToastMessage(exceptionMessageResId.toInt())
        }
        (activity as FragmentNeeds).setRefreshListener { viewModel.list(source) }
        (activity as FragmentNeeds).setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        viewModel.list(source)
    }

    override fun onDestroyView() {
        _binding = null
        _viewModel = null
        super.onDestroyView()
    }
}