package com.yang.simpleplayer.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.SimplePlayerApplication
import com.yang.simpleplayer.databinding.ActivityPlayerBinding
import com.yang.simpleplayer.models.VideoInfo
import com.yang.simpleplayer.utils.Player
import com.yang.simpleplayer.viewmodels.PlayerViewModel

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding: ActivityPlayerBinding get() = requireNotNull(_binding)
    private var _viewModel: PlayerViewModel? = null
    private val viewModel: PlayerViewModel get() = requireNotNull(_viewModel)
    private var _player: Player? = null
    private val player: Player get() = requireNotNull(_player)
    private val videoIdsKey = "videoIds"
    private val videoIdKey = "videoId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoRepo = (application as SimplePlayerApplication).appContainer.videoRepository
        _player = Player.Factory().build(this)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        _viewModel = ViewModelProvider(this, PlayerViewModel.PlayerViewModelFactory(videoRepo, player)).get(PlayerViewModel::class.java)
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
        val videoIds = intent.getLongArrayExtra(videoIdsKey)
        val currentVideoId = intent.getLongExtra(videoIdKey, 0L)
        viewModel.progressVisible.observe(this) { progressVisible ->
            setProgressBar(progressVisible)
        }
        viewModel.exceptionMessageResId.observe(this) { exceptionMessageResId ->
            showToastMessage(getString(exceptionMessageResId.toInt()))
        }
        viewModel.isSetVideo.observe(this) {
            this.player.attachStyledPlayerView(binding.playerView)
            this.player.prepare()
            this.player.play()
        }

        player.eventMediaItemTransitionCallback = { videoInfo: VideoInfo -> viewModel.insertOrReplaceVideoInfo(videoInfo) }
        player.eventVideoSizeChangedCallback = { width, height ->
            requestedOrientation = if(width > height) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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