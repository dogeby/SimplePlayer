package com.yang.simpleplayer.fragments.list.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.databinding.FragmentFolderListBinding
import com.yang.simpleplayer.repositories.VideoRepository
import com.yang.simpleplayer.viewmodels.FolderListViewModel

class FolderListFragment : Fragment() {

    private var _binding: FragmentFolderListBinding? = null
    private val binding: FragmentFolderListBinding get() = requireNotNull(_binding)
    private var _viewModel: FolderListViewModel? = null
    private val viewModel: FolderListViewModel get() = requireNotNull(_viewModel)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val videoRepo = VideoRepository()
        _binding = FragmentFolderListBinding.inflate(layoutInflater)
        try {
            _viewModel = ViewModelProvider(this, FolderListViewModel.FolderListViewModelFactory(videoRepo,
                    (activity as FragmentNeeds).getApplication())).get(FolderListViewModel::class.java)
            initUi()
        } catch (e: Exception) {
            (activity as FragmentNeeds).showToastMessage(e.toString())
        }

        return binding.root
    }

    private fun initUi() {
        val adapter = FolderListAdapter().apply {
            itemViewOnclick = (activity as FragmentNeeds)::startVideoListFragment
            // TODO: morebtn 클릭 리스너 작성
        }
        binding.folderList.adapter = adapter

        viewModel.folderNames.observe(viewLifecycleOwner) { folders ->
            adapter.updateFolders(folders)
        }
        viewModel.progressVisible.observe(viewLifecycleOwner) { progressVisible ->
            (activity as FragmentNeeds).setProgressBar(progressVisible)
        }
        viewModel.exceptionMessage.observe(viewLifecycleOwner) { exceptionMessage ->
            (activity as FragmentNeeds).showToastMessage(exceptionMessage)
        }
        (activity as FragmentNeeds).setRefreshListener { viewModel.update() }
        viewModel.list()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }
}