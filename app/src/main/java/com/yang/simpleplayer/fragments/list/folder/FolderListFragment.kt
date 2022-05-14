package com.yang.simpleplayer.fragments.list.folder

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
import com.yang.simpleplayer.databinding.FragmentFolderListBinding
import com.yang.simpleplayer.viewmodels.FolderListViewModel

class FolderListFragment : Fragment() {

    private var _binding: FragmentFolderListBinding? = null
    private val binding: FragmentFolderListBinding get() = requireNotNull(_binding)
    private var _viewModel: FolderListViewModel? = null
    private val viewModel: FolderListViewModel get() = requireNotNull(_viewModel)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val folderRepository = ( activity?.application as SimplePlayerApplication).appContainer.folderRepository
        _binding = FragmentFolderListBinding.inflate(layoutInflater)
        _viewModel = ViewModelProvider(this, FolderListViewModel.FolderListViewModelFactory(folderRepository)).get(FolderListViewModel::class.java)
        initUi()
        return binding.root
    }

    private fun initUi() {
        val adapter = FolderListAdapter().apply {
            itemViewOnclick = (activity as FragmentNeeds)::startVideoListFragment
            moreBtnOnclick = { folder ->
                /**
                 * folder more 버튼 클릭 시
                 * 플레이리스트에 동영상들 추가
                 */
                val moreBtnStrArr = mutableListOf<String>()
                val callbacks = mutableListOf<()->Unit>()
                moreBtnStrArr.add(getString(R.string.addToPlaylist))
                val videoIds = LongArray(folder.videoIds.size) {folder.videoIds[it]}
                callbacks.add {(activity as FragmentNeeds).startPlaylistManageActivity(videoIds)}
                context?.let { MoreDialogFactory.create(it, moreBtnStrArr.toTypedArray(), *callbacks.toTypedArray()).show() }
            }
        }
        binding.folderList.adapter = adapter

        viewModel.folders.observe(viewLifecycleOwner) { folders ->
            adapter.updateFolders(folders)
        }
//        viewModel.progressVisible.observe(viewLifecycleOwner) { progressVisible ->
//            (activity as FragmentNeeds).setProgressBar(progressVisible)
//        }
        viewModel.exceptionMessageResId.observe(viewLifecycleOwner) { exceptionMessageResId ->
            (activity as FragmentNeeds).showToastMessage(exceptionMessageResId.toInt())
        }
        (activity as FragmentNeeds).setRefreshListener { viewModel.list() }
        (activity as FragmentNeeds).setOnQueryTextListener(object:SearchView.OnQueryTextListener{
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