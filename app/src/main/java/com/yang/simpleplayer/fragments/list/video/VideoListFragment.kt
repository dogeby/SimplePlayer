package com.yang.simpleplayer.fragments.list.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.databinding.FragmentVideoListBinding
import com.yang.simpleplayer.repositories.VideoRepository
import com.yang.simpleplayer.utils.Format
import com.yang.simpleplayer.viewmodels.VideoListViewModel

class VideoListFragment : Fragment() {

    private var _binding: FragmentVideoListBinding? = null
    private val binding: FragmentVideoListBinding get() = requireNotNull(_binding)
    private var _viewModel: VideoListViewModel? = null
    private val viewModel: VideoListViewModel get() = requireNotNull(_viewModel)
    private var _source: Any? = null
    private val source: Any get() = requireNotNull(_source)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val videoRepo = VideoRepository()
        _binding = FragmentVideoListBinding.inflate(layoutInflater)
        arguments?.let {
            _source = it.getString(getString(R.string.folderNameKey))
                // TODO: id 받을시 코드 작성
        }
        _viewModel = ViewModelProvider(this, VideoListViewModel.VideoListViewModelFactory(videoRepo,
        (activity as FragmentNeeds).getApplication())).get(VideoListViewModel::class.java)
        initUi()
        return binding.root
    }

    private fun initUi() {
        val adapter = VideoListAdapter().apply {
            itemViewOnclick = (activity as FragmentNeeds)::startPlayerActivity
            // TODO: morebtn 동작 코드 작성
        }
        binding.videoList.adapter = adapter

        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            adapter.updateVideos(videos)
        }
        viewModel.progressVisible.observe(viewLifecycleOwner) { progressVisible ->
            (activity as FragmentNeeds).setProgressBar(progressVisible)
        }
        viewModel.exceptionMessageResId.observe(viewLifecycleOwner) { exceptionMessageResId ->
            (activity as FragmentNeeds).showToastMessage(getString(exceptionMessageResId.toInt()))
        }
        (activity as FragmentNeeds).setRefreshListener { viewModel.update(source) }
        (activity as FragmentNeeds).setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        (activity as FragmentNeeds).setAppbarTitleText(Format.getParentFolderName(source.toString()))
        viewModel.list(source)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }
}