package com.yang.simpleplayer.fragments.list.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.SimplePlayerApplication
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.common.MoreDialogFactory
import com.yang.simpleplayer.databinding.FragmentVideoListBinding
import com.yang.simpleplayer.viewmodels.VideoListViewModel

// TODO: 플레이 리스트 동영상 이름순 정렬인거 해결
class VideoListFragment : Fragment() {

    private var _binding: FragmentVideoListBinding? = null
    private val binding: FragmentVideoListBinding get() = requireNotNull(_binding)
    private var _viewModel: VideoListViewModel? = null
    private val viewModel: VideoListViewModel get() = requireNotNull(_viewModel)
    private var _source: Any? = null
    private val source: Any get() = requireNotNull(_source)
    private val folderNameKey = "folderName"
    private val playlistIdKey = "playlistId"
    private val emptyView: TextView by lazy {    //폴더리스트는 동영상이 있어야 폴더로 나타나기 때문에 플레이리스트 비어있는 경우만 고려
        (layoutInflater.inflate(R.layout.view_empty_list, null) as TextView).apply {
            setText(R.string.empty_playlist)
        }
    }

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
                moreBtnStrArr.add(getString(R.string.add_to_playlist))
                callbacks.add {(activity as FragmentNeeds).startPlaylistManageActivity(longArrayOf(video.id))}
                if(source is Long) {
                    moreBtnStrArr.add(getString(R.string.video_delete_from_playlist))
                    callbacks.add {
                        viewModel.deleteVideoFromPlaylist(video.id, source as Long)
                        viewModel.list(source)
                    }
                }
                context?.let { MoreDialogFactory.create(it, moreBtnStrArr.toTypedArray(), *callbacks.toTypedArray()).show() }
            }
        }
        binding.videoList.adapter = adapter

        val rootViewSize = binding.root.size
        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            if(videos.isNotEmpty()) {
                if(binding.root.size > rootViewSize) binding.root.removeView(emptyView)
                adapter.updateVideos(videos)

            } else if(binding.root.size == rootViewSize) {  //리스트가 비어있는 경우
                binding.root.addView(emptyView)
            }
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