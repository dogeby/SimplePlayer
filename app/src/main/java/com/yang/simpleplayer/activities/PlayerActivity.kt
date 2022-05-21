package com.yang.simpleplayer.activities

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yang.simpleplayer.Preferences.thema.ControllerThema
import com.yang.simpleplayer.R
import com.yang.simpleplayer.SimplePlayerApplication
import com.yang.simpleplayer.databinding.ActivityPlayerBinding
import com.yang.simpleplayer.databinding.ViewTouchPlayerBinding
import com.yang.simpleplayer.models.VideoInfo
import com.yang.simpleplayer.utils.Player
import com.yang.simpleplayer.viewmodels.PlayerViewModel
import com.yang.simpleplayer.views.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** pip mode 참고 샘플: https://github.com/android/media-samples/tree/main/PictureInPictureKotlin */
/** Intent action for player controls from Picture-in-Picture mode.  */
private const val ACTION_PLAYER_CONTROL = "player_control"

/** Intent extra for player controls from Picture-in-Picture mode.  */
private const val EXTRA_CONTROL_TYPE = "control_type"
private const val CONTROL_TYPE_PLAY_OR_PAUSE = 1
private const val REQUEST_PLAY_OR_PAUSE = 2

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding: ActivityPlayerBinding get() = requireNotNull(_binding)
    private var _viewModel: PlayerViewModel? = null
    private val viewModel: PlayerViewModel get() = requireNotNull(_viewModel)
    private var _player: Player? = null
    private val player: Player get() = requireNotNull(_player)
    private var _playerView:StyledPlayerView? = null
    private val playerView:StyledPlayerView get() = requireNotNull(_playerView)
    private val videoIdsKey = "videoIds"
    private val videoIdKey = "videoId"
    private var pipBroadcastReceiver = buildBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoRepo = (application as SimplePlayerApplication).appContainer.videoRepository
        val userPreferencesRepo = (application as SimplePlayerApplication).appContainer.userPreferencesRepository
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        _viewModel = ViewModelProvider(this, PlayerViewModel.PlayerViewModelFactory(videoRepo, userPreferencesRepo)).get(PlayerViewModel::class.java)
        hideSystemBars()
        initUi()
        registerReceiver(pipBroadcastReceiver, IntentFilter(ACTION_PLAYER_CONTROL))
        setContentView(binding.root)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        unregisterReceiver(pipBroadcastReceiver)
        player.release()
        _player = null
        playerView.player = null
        this.intent = intent
        val videoIds = this.intent.getLongArrayExtra(videoIdsKey)
        val currentVideoId = this.intent.getLongExtra(videoIdKey, 0L)
        buildPlayer()
        pipBroadcastReceiver = buildBroadcastReceiver()
        registerReceiver(pipBroadcastReceiver, IntentFilter(ACTION_PLAYER_CONTROL))
        viewModel.requestPlayer(currentVideoId, requireNotNull(videoIds))
    }

    override fun onRestart() {
        super.onRestart()
        playerView.showController()
    }

    override fun onPause() {
        if(isInPictureInPictureMode && player.isPlaying()) player.play()
        else player.pause()
        super.onPause()
    }

    override fun onStop() {
        player.stop()
        super.onStop()
    }

    override fun onDestroy() {
        unregisterReceiver(pipBroadcastReceiver)
        player.release()
        _player = null
        playerView.player = null
        super.onDestroy()
    }

    override fun onUserLeaveHint() {
        minimize()
        super.onUserLeaveHint()
    }

    private fun initUi() {
        val videoIds = intent.getLongArrayExtra(videoIdsKey)
        val currentVideoId = intent.getLongExtra(videoIdKey, 0L)
//        viewModel.progressVisible.observe(this) { progressVisible ->
//            setProgressBar(progressVisible)
//        }
        //설정 controller 테마에 따라 플레이어 controller 설정
        lifecycleScope.launch(Dispatchers.IO) {
            val userPreferences = viewModel.getUserPreferences()
            withContext(Dispatchers.Main) {
                buildPlayer()
                when(userPreferences.controllerThema) {
                    ControllerThema.BUTTON.ordinal -> {
                        _playerView = layoutInflater.inflate(R.layout.view_btn_player, null) as StyledPlayerView?
                        binding.playerContainer.addView(playerView)
                    }
                    ControllerThema.TOUCH.ordinal -> {
                        val touchPlayerBinding = ViewTouchPlayerBinding.inflate(layoutInflater, binding.playerContainer, true)
                        _playerView = touchPlayerBinding.playerView
                        (playerView as PlayerView).ableDoubleTabEvent(touchPlayerBinding.rewWithAmount, touchPlayerBinding.ffwdWithAmount, touchPlayerBinding.touchViewContainer)
                    }
                }
                viewModel.requestPlayer(currentVideoId, requireNotNull(videoIds))
            }
        }
        viewModel.playerPlaylist.observe(this@PlayerActivity) { playerPlaylist ->
            player.setMediaItems(playerPlaylist.videos, playerPlaylist.currentVideoIndex)
            player.attachStyledPlayerView(playerView)
            player.prepare()
            player.play()
        }
        viewModel.exceptionMessageResId.observe(this) { exceptionMessageResId ->
            showToastMessage(exceptionMessageResId.toInt())
        }
    }

    private fun buildPlayer() {
        _player = Player.Factory().build(this@PlayerActivity).apply {
            eventMediaItemTransitionCallback = { videoInfo: VideoInfo ->
                viewModel.insertOrReplaceVideoInfo(videoInfo) }
            eventVideoSizeChangedCallback = { width, height ->
                requestedOrientation = if(width > height) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    private fun updatePictureInPictureParams(isPlaying:Boolean): PictureInPictureParams {
        val aspectRatio = Rational(playerView.width, playerView.height)
        val visibleRect = Rect()
        val params = PictureInPictureParams.Builder()
            .setActions(
                listOf(
                    if (isPlaying) {
                        createRemoteAction(
                            R.drawable.ic_baseline_pause_24,
                            R.string.pause,
                            REQUEST_PLAY_OR_PAUSE,
                            CONTROL_TYPE_PLAY_OR_PAUSE
                        )
                    } else {
                        createRemoteAction(
                            R.drawable.ic_baseline_play_arrow_24,
                            R.string.play,
                            REQUEST_PLAY_OR_PAUSE,
                            CONTROL_TYPE_PLAY_OR_PAUSE
                        )
                    }
                )
            )
            .setAspectRatio(aspectRatio)
            .setSourceRectHint(visibleRect)
//            .setAutoEnterEnabled(true) @RequiresApi(Build.VERSION_CODES.S)
            .build()
        setPictureInPictureParams(params)
        return params
    }

    private fun minimize() {
        playerView.hideController()
        enterPictureInPictureMode(updatePictureInPictureParams(player.isPlaying()))
    }

    private fun buildBroadcastReceiver() = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action != ACTION_PLAYER_CONTROL) {
                return
            }
            when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                CONTROL_TYPE_PLAY_OR_PAUSE -> {
                    if (player.isPlaying()) {
                        player.pause()
                        updatePictureInPictureParams(false)
                    } else {
                        player.play()
                        updatePictureInPictureParams(true)
                    }
                }
            }
        }
    }

    /**
     * Creates a [RemoteAction]. It is used as an action icon on the overlay of the
     * picture-in-picture mode.
     */
    private fun createRemoteAction(
        @DrawableRes iconResId: Int,
        @StringRes titleResId: Int,
        requestCode: Int,
        controlType: Int
    ): RemoteAction {
        return RemoteAction(
            Icon.createWithResource(this, iconResId),
            getString(titleResId),
            getString(titleResId),
            PendingIntent.getBroadcast(
                this,
                requestCode,
                Intent(ACTION_PLAYER_CONTROL)
                    .putExtra(EXTRA_CONTROL_TYPE, controlType),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun showToastMessage(resId:Int) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
    }
    private fun setProgressBar(visible:Boolean) {
        binding.progressBar.visibility = if(visible) View.VISIBLE else View.GONE
    }
}