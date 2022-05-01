package com.yang.simpleplayer.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.databinding.ActivityPlayerBinding
import com.yang.simpleplayer.repositories.VideoRepository
import com.yang.simpleplayer.utils.Player
import com.yang.simpleplayer.viewmodels.PlayerViewModel

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding: ActivityPlayerBinding get() = requireNotNull(_binding)
    private var _viewModel: PlayerViewModel? = null
    private val viewModel: PlayerViewModel get() = requireNotNull(_viewModel)
    private var _player: Player? = null
    private val player: Player get() = requireNotNull(_player)
    private val VIDEO_IDS_KEY = "videoIds"
    private val VIDEO_ID_KEY = "videoId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoRepo = VideoRepository()
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        _viewModel = ViewModelProvider(this, PlayerViewModel.PlayerViewModelFactory(videoRepo,
            application)).get(PlayerViewModel::class.java)
        hideSystemBars()
        initUi()
        setContentView(binding.root)
    }

    override fun onPause() {
        player.pause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
        _player = null
        binding.playerView.player = null
    }

    private fun initUi() {
        val videoIds = intent.getLongArrayExtra(VIDEO_IDS_KEY)
        val currentVideoId = intent.getLongExtra(VIDEO_ID_KEY, 0L)
        viewModel.player.observe(this) { player ->
            _player = player
            this.player.attachStyledPlayerView(binding.playerView)
            this.player.prepare()
            this.player.play()
        }
        viewModel.progressVisible.observe(this) { progressVisible ->
            setProgressBar(progressVisible)
        }
        viewModel.exceptionMessageResId.observe(this) { exceptionMessageResId ->
            showToastMessage(getString(exceptionMessageResId.toInt()))
        }
        viewModel.requestPlayer(currentVideoId, requireNotNull(videoIds))
    }

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun showToastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    private fun setProgressBar(visible:Boolean) {
        binding.progressBar.visibility = if(visible) View.VISIBLE else View.GONE
    }
}