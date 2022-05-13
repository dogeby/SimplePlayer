package com.yang.simpleplayer.fragments.list.recent

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
import com.yang.simpleplayer.fragments.list.video.VideoListAdapter
import com.yang.simpleplayer.viewmodels.RecentListViewModel

/**
 * 임시로 videoAdapter, videoListBinding사용
 */
// TODO: 날짜 넣을수있는 어뎁터 작성 필요
// TODO: 최근 비디오 비어있으면 비었다는 뷰 띄우기
class RecentVideoListFragment : Fragment() {

    private var _binding: FragmentVideoListBinding? = null
    private val binding: FragmentVideoListBinding get() = requireNotNull(_binding)
    private var _viewModel: RecentListViewModel? = null
    private val viewModel: RecentListViewModel get() = requireNotNull(_viewModel)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val videoRepo = ( activity?.application as SimplePlayerApplication).appContainer.videoRepository
        _binding = FragmentVideoListBinding.inflate(layoutInflater)
        _viewModel = ViewModelProvider(this, RecentListViewModel.RecentListViewModelFactory(videoRepo)).get(RecentListViewModel::class.java)
        initUi()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // TODO: RecyclerView 최적화 필요
        viewModel.list()
    }

    private fun initUi() {
        val adapter = VideoListAdapter().apply {
            itemViewOnclick = (activity as FragmentNeeds)::startPlayerActivity
            moreBtnOnClick = { video ->
                /**
                 * video more 버튼 클릭시
                 * 플레이리스트에 동영상 추가를
                 * 최근 기록에서 동영상 기록 제거
                 */
                val moreBtnStrArr = mutableListOf<String>()
                val callbacks = mutableListOf<()->Unit>()
                moreBtnStrArr.add(getString(R.string.videoAddToPlaylist))
                callbacks.add {(activity as FragmentNeeds).startPlaylistManageActivity(longArrayOf(video.id))}
                moreBtnStrArr.add(getString(R.string.videoDeleteFromRecentList))
                callbacks.add {
                    viewModel.deletePlaybackDate(video.id)
                    viewModel.list()
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
        (activity as FragmentNeeds).setRefreshListener { viewModel.list() }
        (activity as FragmentNeeds).setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        viewModel.list()
    }

    override fun onDestroyView() {
        _binding = null
        _viewModel = null
        super.onDestroyView()
    }
}