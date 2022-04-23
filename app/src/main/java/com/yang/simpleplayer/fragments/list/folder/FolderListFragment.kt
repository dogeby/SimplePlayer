package com.yang.simpleplayer.fragments.list.folder

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.yang.simpleplayer.activities.list.FragmentNeeds
import com.yang.simpleplayer.activities.list.ListActivity
import com.yang.simpleplayer.databinding.FragmentFolderListBinding
import com.yang.simpleplayer.repositories.FolderRepository
import com.yang.simpleplayer.viewmodels.FolderListViewModel
import java.lang.Exception

class FolderListFragment : Fragment() {

    private var _binding: FragmentFolderListBinding? = null
    private val binding: FragmentFolderListBinding get() = requireNotNull(_binding)
    private var _viewModel: FolderListViewModel? = null
    private val viewModel: FolderListViewModel get() = requireNotNull(_viewModel)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val folderRepo = FolderRepository()
        _binding = FragmentFolderListBinding.inflate(layoutInflater)
        try {
            _viewModel = ViewModelProvider(this, FolderListViewModel.FolderListViewModelFactory(folderRepo,
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

        viewModel.folders.observe(viewLifecycleOwner) { folders ->
            adapter.updateFolders(folders)
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