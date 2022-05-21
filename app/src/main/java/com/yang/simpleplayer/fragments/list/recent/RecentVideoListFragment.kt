package com.yang.simpleplayer.fragments.list.recent

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
import com.yang.simpleplayer.viewmodels.RecentListViewModel

class RecentVideoListFragment : Fragment() {

    private var _binding: FragmentVideoListBinding? = null
    private val binding: FragmentVideoListBinding get() = requireNotNull(_binding)
    private var _viewModel: RecentListViewModel? = null
    private val viewModel: RecentListViewModel get() = requireNotNull(_viewModel)
    private val emptyView: TextView by lazy {
        (layoutInflater.inflate(R.layout.view_empty_list, null) as TextView).apply {
            setText(R.string.empty_recentList)
        }
    }

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
        val adapter = RecentVideoListAdapter().apply {
            itemViewOnclick = (activity as FragmentNeeds)::startPlayerActivity
            moreBtnOnClick = { video ->
                /**
                 * video more 버튼 클릭시
                 * 플레이리스트에 동영상 추가를
                 * 최근 기록에서 동영상 기록 제거
                 */
                val moreBtnStrArr = mutableListOf<String>()
                val callbacks = mutableListOf<()->Unit>()
                moreBtnStrArr.add(getString(R.string.add_to_playlist))
                callbacks.add {(activity as FragmentNeeds).startPlaylistManageActivity(longArrayOf(video.id))}
                moreBtnStrArr.add(getString(R.string.video_delete_from_recentList))
                callbacks.add {
                    viewModel.deletePlaybackDate(video.id)
                    viewModel.list()
                }
                context?.let { MoreDialogFactory.create(it, moreBtnStrArr.toTypedArray(), *callbacks.toTypedArray()).show() }
            }
        }
        binding.videoList.adapter = adapter

        val rootViewSize = binding.root.size
        viewModel.recentVideoItems.observe(viewLifecycleOwner) { recentVideoItems ->
            if(recentVideoItems.isNotEmpty()) {
                if(binding.root.size > rootViewSize) binding.root.removeView(emptyView)
            } else if(binding.root.size == rootViewSize) {  //리스트가 비어있는 경우
                binding.root.addView(emptyView)
            }
            adapter.updateVideos(recentVideoItems)
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
    }

    override fun onDestroyView() {
        _binding = null
        _viewModel = null
        super.onDestroyView()
    }
}