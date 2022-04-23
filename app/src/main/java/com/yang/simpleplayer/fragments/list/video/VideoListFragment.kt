package com.yang.simpleplayer.fragments.list.video

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.activities.list.ListActivity
import com.yang.simpleplayer.databinding.FragmentVideoListBinding
import com.yang.simpleplayer.repositories.VideoRepository
import com.yang.simpleplayer.viewmodels.FolderListViewModel
import com.yang.simpleplayer.viewmodels.VideoListViewModel

class VideoListFragment : Fragment() {

    private var _binding: FragmentVideoListBinding? = null
    private val binding: FragmentVideoListBinding get() = requireNotNull(_binding)
    private var _viewModel: VideoListViewModel? = null
    private val viewModel: VideoListViewModel get() = requireNotNull(_viewModel)
    private var _videoIds: LongArray? = null
    private val videoIds: LongArray get() = requireNotNull(_videoIds)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val videoRepo = VideoRepository()
        _binding = FragmentVideoListBinding.inflate(layoutInflater)
        try {
            arguments?.let {
                _videoIds = it.getLongArray(R.string.videoIdsKey.toString())
            }
            _viewModel = ViewModelProvider(this, VideoListViewModel.VideoListViewModelFactory(videoRepo, videoIds,
                    (activity as FragmentNeeds).getApplication())).get(VideoListViewModel::class.java)
            initUi()
        } catch (e: Exception) {
            (activity as FragmentNeeds).showToastMessage(e.toString())
        }
        return binding.root
    }

    private fun initUi() {
        val adapter = VideoListAdapter()
        binding.videoList.adapter = adapter

        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            adapter.updateVideos(videos)
        }
        viewModel.progressVisible.observe(viewLifecycleOwner) { progressVisible ->
            (activity as FragmentNeeds).setProgressBar(progressVisible)
        }
        viewModel.exceptionMessage.observe(viewLifecycleOwner) { exceptionMessage ->
            (activity as FragmentNeeds).showToastMessage(exceptionMessage)
        }
        (activity as FragmentNeeds).setRefreshListener { viewModel.update() }
        viewModel.init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }
}