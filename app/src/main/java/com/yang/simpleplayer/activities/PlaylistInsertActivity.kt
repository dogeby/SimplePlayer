package com.yang.simpleplayer.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.SimplePlayerApplication
import com.yang.simpleplayer.databinding.ActivityPlaylistInsertBinding
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.viewmodels.PlaylistInsertViewModel

class PlaylistInsertActivity : AppCompatActivity() {
    private var _binding:ActivityPlaylistInsertBinding? = null
    private val binding: ActivityPlaylistInsertBinding get() = requireNotNull(_binding)
    private var _viewModel:PlaylistInsertViewModel? = null
    private val viewModel:PlaylistInsertViewModel get() = requireNotNull(_viewModel)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playlistRepo = (application as SimplePlayerApplication).appContainer.playlistRepository
        _binding = ActivityPlaylistInsertBinding.inflate(layoutInflater)
        _viewModel = ViewModelProvider(this, PlaylistInsertViewModel.PlaylistInsertViewModelFactory(playlistRepo)).get(PlaylistInsertViewModel::class.java)
        setContentView(binding.root)
        initUi()
    }
    private fun initUi() {
        binding.insertBtn.setOnClickListener {
            viewModel.insertPlaylist(Playlist(binding.playlistNameEt.text.toString()))
        }
        viewModel.isInsertSuccess.observe(this) { isInsertSuccess ->
            if(isInsertSuccess) finish()
            else showToastMessage(getString(R.string.name_duplicate))
        }

    }
    private fun showToastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}