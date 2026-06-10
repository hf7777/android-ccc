package com.hlc.mywallet.feature.tutorial.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hjq.toast.Toaster
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.LayoutTutorialVideoPlayerBinding
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer

/**
 * 教程 MP4 直链播放器：ExoPlayer + 比例适配 + 悬浮控件显隐。
 * 供 [TutorialVideoFragment] 等宿主复用。
 */
class TutorialVideoPlayerController(
    private val binding: LayoutTutorialVideoPlayerBinding,
    private val context: Context,
    lifecycleOwner: LifecycleOwner,
    private val videoUrl: String,
    /** 为 true 时由外部根据视频比例调整容器高度，画面铺满容器 */
    private val adjustContainerAspectExternally: Boolean = false,
) : DefaultLifecycleObserver {

    /** 视频宽高比（宽/高），供嵌入场景调整外层容器 */
    var onVideoAspectRatioReady: ((aspectRatio: Float) -> Unit)? = null

    private var player: ExoPlayer? = null
    private var lastNotifiedAspectRatio = 0f
    private var isUserSeeking = false
    private var controlsVisible = true
    private var isReleased = false
    private var hasFallbackToBrowser = false

    private val progressRunnable = Runnable { updateProgressUi() }
    private val hideControlsRunnable = Runnable { setControlsVisible(false, animate = true) }
    private val controlViews: List<View> = listOf(
        binding.btnPlayPauseCenter,
        binding.overlayBottom,
    )

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun bind() {
        if (videoUrl.isBlank()) {
            Toaster.show(context.getString(R.string.video_not_available))
            return
        }
        setupSurface()
        setupVideoContainerLayoutListener()
        setupControls()
        setupOverlayToggle()
        initPlayer()
        setControlsVisible(true, animate = false)
        scheduleAutoHideControls()
    }

    override fun onStart(owner: LifecycleOwner) {
        player?.playWhenReady = true
    }

    override fun onStop(owner: LifecycleOwner) {
        cancelAutoHideControls()
        binding.root.removeCallbacks(progressRunnable)
        player?.playWhenReady = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    fun release() {
        if (isReleased) {
            return
        }
        isReleased = true
        cancelAutoHideControls()
        binding.root.removeCallbacks(progressRunnable)
        player?.release()
        player = null
    }

    private fun setupOverlayToggle() {
        binding.touchOverlay.setOnClickListener { onScreenTapped() }
    }

    private fun onScreenTapped() {
        if (controlsVisible) {
            setControlsVisible(false, animate = true)
            cancelAutoHideControls()
        } else {
            setControlsVisible(true, animate = true)
            scheduleAutoHideControls()
        }
    }

    private fun setControlsVisible(visible: Boolean, animate: Boolean) {
        controlsVisible = visible
        controlViews.forEach { view ->
            view.animate().cancel()
            if (!animate) {
                view.alpha = if (visible) 1f else 0f
                view.isVisible = visible
                return@forEach
            }
            if (visible) {
                view.isVisible = true
                view.alpha = 0f
                view.animate()
                    .alpha(1f)
                    .setDuration(CONTROLS_FADE_DURATION_MS)
                    .start()
            } else {
                view.animate()
                    .alpha(0f)
                    .setDuration(CONTROLS_FADE_DURATION_MS)
                    .withEndAction {
                        if (!controlsVisible) {
                            view.isVisible = false
                        }
                    }
                    .start()
            }
        }
    }

    private fun scheduleAutoHideControls() {
        if (!controlsVisible) {
            return
        }
        cancelAutoHideControls()
        binding.root.postDelayed(hideControlsRunnable, CONTROLS_AUTO_HIDE_MS)
    }

    private fun cancelAutoHideControls() {
        binding.root.removeCallbacks(hideControlsRunnable)
    }

    private fun setupVideoContainerLayoutListener() {
        binding.videoContainer.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            player?.videoSize?.let { applySurfaceLayout(it) }
        }
    }

    private fun setupSurface() {
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                player?.setVideoSurfaceHolder(holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                player?.clearVideoSurfaceHolder(holder)
            }
        })
    }

    private fun setupControls() {
        binding.btnPlayPauseCenter.onClick {
            togglePlayPause()
            scheduleAutoHideControls()
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.tvCurrent.text = formatDuration(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
                cancelAutoHideControls()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
                player?.seekTo(seekBar?.progress?.toLong() ?: 0L)
                scheduleAutoHideControls()
            }
        })
    }

    private fun initPlayer() {
        val exoPlayer = ExoPlayer.Builder(context).build()
        player = exoPlayer
        exoPlayer.addListener(playerListener)
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        scheduleProgressUpdate()
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> binding.progressLoading.isVisible = true
                Player.STATE_READY -> {
                    binding.progressLoading.isVisible = false
                    val duration = player?.duration ?: 0L
                    if (duration > 0) {
                        binding.seekBar.max = duration.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                        binding.tvDuration.text = formatDuration(duration)
                    }
                    player?.videoSize?.let { size ->
                        binding.videoContainer.post { applySurfaceLayout(size) }
                    }
                    updatePlayPauseIcon()
                }
                Player.STATE_ENDED -> {
                    binding.progressLoading.isVisible = false
                    updatePlayPauseIcon()
                }
                else -> binding.progressLoading.isVisible = false
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayPauseIcon()
            if (isPlaying) {
                scheduleProgressUpdate()
            } else {
                binding.root.removeCallbacks(progressRunnable)
            }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            binding.videoContainer.post { applySurfaceLayout(videoSize) }
        }

        override fun onPlayerError(error: PlaybackException) {
            binding.progressLoading.isVisible = false
            fallbackToSystemBrowser()
        }
    }

    /** 应用内播放失败时，使用系统浏览器打开视频链接 */
    private fun fallbackToSystemBrowser() {
        if (hasFallbackToBrowser) {
            return
        }
        hasFallbackToBrowser = true
        player?.playWhenReady = false
        val url = videoUrl.trim()
        if (url.isBlank()) {
            Toaster.show(context.getString(R.string.video_not_available))
            return
        }
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }.onFailure {
            Toaster.show(context.getString(R.string.video_playback_failed))
        }
    }

    private fun togglePlayPause() {
        val exoPlayer = player ?: return
        if (exoPlayer.playbackState == Player.STATE_ENDED) {
            exoPlayer.seekTo(0)
            exoPlayer.play()
            return
        }
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    private fun updatePlayPauseIcon() {
        val exoPlayer = player ?: return
        val iconRes = if (exoPlayer.isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        binding.btnPlayPauseCenter.setImageResource(iconRes)
    }

    private fun scheduleProgressUpdate() {
        binding.root.removeCallbacks(progressRunnable)
        binding.root.postDelayed(progressRunnable, PROGRESS_INTERVAL_MS)
    }

    private fun updateProgressUi() {
        val exoPlayer = player ?: return
        if (!isUserSeeking) {
            val position = exoPlayer.currentPosition
            binding.tvCurrent.text = formatDuration(position)
            binding.seekBar.progress = position.coerceAtMost(binding.seekBar.max.toLong()).toInt()
        }
        if (exoPlayer.isPlaying || exoPlayer.playbackState == Player.STATE_BUFFERING) {
            scheduleProgressUpdate()
        }
    }

    private fun applySurfaceLayout(videoSize: VideoSize) {
        val videoAspect = resolveVideoAspectRatio(videoSize)
        if (videoAspect <= 0f) {
            return
        }
        if (adjustContainerAspectExternally) {
            notifyVideoAspectRatio(videoAspect)
            applySurfaceFillContainer()
        } else {
            updateSurfaceViewFit(videoSize, videoAspect)
        }
    }

    private fun notifyVideoAspectRatio(aspectRatio: Float) {
        if (aspectRatio == lastNotifiedAspectRatio) {
            return
        }
        lastNotifiedAspectRatio = aspectRatio
        onVideoAspectRatioReady?.invoke(aspectRatio)
    }

    private fun applySurfaceFillContainer() {
        val layoutParams = binding.surfaceView.layoutParams as FrameLayout.LayoutParams
        if (layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT &&
            layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT
        ) {
            return
        }
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.gravity = Gravity.CENTER
        binding.surfaceView.layoutParams = layoutParams
    }

    /** 在固定比例容器内居中适配（FIT），两侧或上下留白 */
    private fun updateSurfaceViewFit(videoSize: VideoSize, videoAspect: Float) {
        val container = binding.videoContainer
        val containerWidth = container.width
        val containerHeight = container.height
        if (containerWidth <= 0 || containerHeight <= 0) {
            return
        }

        val containerAspect = containerWidth.toFloat() / containerHeight
        val (surfaceWidth, surfaceHeight) = if (videoAspect > containerAspect) {
            containerWidth to (containerWidth / videoAspect).toInt()
        } else {
            (containerHeight * videoAspect).toInt() to containerHeight
        }

        val layoutParams = binding.surfaceView.layoutParams as FrameLayout.LayoutParams
        if (layoutParams.width == surfaceWidth && layoutParams.height == surfaceHeight) {
            return
        }
        layoutParams.width = surfaceWidth
        layoutParams.height = surfaceHeight
        layoutParams.gravity = Gravity.CENTER
        binding.surfaceView.layoutParams = layoutParams
    }

    private fun resolveVideoAspectRatio(videoSize: VideoSize): Float {
        var width = videoSize.width
        var height = videoSize.height
        if (width <= 0 || height <= 0) {
            return 0f
        }
        when (videoSize.unappliedRotationDegrees) {
            90, 270 -> {
                val swapped = width
                width = height
                height = swapped
            }
        }
        val pixelRatio = videoSize.pixelWidthHeightRatio
        return if (pixelRatio > 0f) {
            width * pixelRatio / height
        } else {
            width.toFloat() / height
        }
    }

    private fun formatDuration(positionMs: Long): String {
        val totalSeconds = (positionMs / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    companion object {
        private const val PROGRESS_INTERVAL_MS = 500L
        private const val CONTROLS_AUTO_HIDE_MS = 3000L
        private const val CONTROLS_FADE_DURATION_MS = 300L
    }
}
