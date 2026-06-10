package com.hlc.mywallet.feature.tutorial

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.loadRounded
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.mywallet.R
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.TutorialResp
import com.hlc.mywallet.databinding.ActivityTutorialDetailBinding

class TutorialDetailActivity : BaseVbActivity<ActivityTutorialDetailBinding>(),
    TutorialVideoFragment.OnVideoAspectRatioListener {

    private var tutorial: TutorialResp? = null
    private var isVideoPlayerShown = false
    private var videoAspectRatio = 0f
    private var coverHeightAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        tutorial = intent.getParcelableExtra(Constants.RouterKeys.TUTORIAL)
        super.onCreate(savedInstanceState)
    }

    override fun initImmersionBar() {
        immersionBar {
            transparentStatusBar()
            transparentNavigationBar()
            statusBarDarkFont(false)
            navigationBarDarkIcon(true)
            fitsSystemWindows(false)
        }
    }

    override fun useBaseTitleBar(): Boolean = false

    override fun getBaseTitleBarTitle(): String {
        return tutorial?.title?.takeIf { it.isNotBlank() } ?: getString(R.string.tutorial)
    }

    override fun initView() {
        val currentTutorial = tutorial
        if (currentTutorial == null) {
            Toaster.show(getString(R.string.request_failed))
            finish()
            return
        }

        bindTutorial(currentTutorial)
        val videoUrl = currentTutorial.videoUrl
        val videoCoverUrl = currentTutorial.coverImage
        if (!videoUrl.isNullOrBlank()) {
            showEmbeddedVideoPlayer(videoUrl, videoCoverUrl)
        } else {
            binding.btnPlayVideo.onClick { }
            binding.ivCover.onClick { }
            binding.ivPlayMask.onClick { }
        }

        binding.ivFinish.onClick {
            finish()
        }
        applyImmersiveInsets()
    }

    private fun applyImmersiveInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBarInset = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            val finishParams = binding.ivFinish.layoutParams as ConstraintLayout.LayoutParams
            finishParams.topMargin = 15.dp + statusBarInset.top
            finishParams.marginStart = 15.dp + statusBarInset.left
            binding.ivFinish.layoutParams = finishParams

            binding.llContent.setPadding(
                binding.llContent.paddingLeft,
                binding.llContent.paddingTop,
                binding.llContent.paddingRight,
                CONTENT_BOTTOM_PADDING_DP.dp + navigationBarInset.bottom
            )
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    override fun onVideoAspectRatioReady(aspectRatio: Float) {
        if (aspectRatio <= 0f) {
            return
        }
        videoAspectRatio = aspectRatio
        updateCoverContainerHeight()
    }

    private fun updateCoverContainerHeight() {
        val aspectRatio = videoAspectRatio
        if (aspectRatio <= 0f) {
            return
        }
        binding.coverContainer.post {
            val width = binding.coverContainer.width
            if (width <= 0) {
                binding.coverContainer.viewTreeObserver.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            binding.coverContainer.viewTreeObserver
                                .removeOnGlobalLayoutListener(this)
                            updateCoverContainerHeight()
                        }
                    }
                )
                return@post
            }
            val targetHeight = (width / aspectRatio).toInt().coerceAtLeast(1)
            animateCoverContainerHeight(targetHeight)
        }
    }

    private fun animateCoverContainerHeight(targetHeight: Int) {
        val container = binding.coverContainer
        val layoutParams = container.layoutParams
        val startHeight = when {
            layoutParams.height > 0 -> layoutParams.height
            container.height > 0 -> container.height
            else -> 200.dp
        }
        if (startHeight == targetHeight) {
            return
        }

        coverHeightAnimator?.cancel()
        coverHeightAnimator = ValueAnimator.ofInt(startHeight, targetHeight).apply {
            duration = COVER_HEIGHT_ANIM_DURATION_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                layoutParams.height = animator.animatedValue as Int
                container.layoutParams = layoutParams
            }
            start()
        }
    }

    override fun onDestroy() {
        coverHeightAnimator?.cancel()
        coverHeightAnimator = null
        super.onDestroy()
    }

    private fun bindTutorial(tutorial: TutorialResp) {
        binding.apply {
            ivCover.loadRounded(tutorial.coverImage ?: R.drawable.ic_logo_big, 8)
            tvTitle.text = tutorial.title.orEmpty()
            tvSubtitle.text = tutorial.subtitle.orEmpty()
            tvDate.text = tutorial.updateTime.orEmpty()
            tvContent.text = HtmlCompat.fromHtml(
                tutorial.content.orEmpty(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            tvSubtitle.visibleOrGone(tutorial.subtitle.isNullOrBlank().not())
            tvDate.visibleOrGone(tutorial.updateTime.isNullOrBlank().not())
            tvContent.visibleOrGone(tutorial.content.isNullOrBlank().not())
            btnPlayVideo.visibleOrGone(tutorial.videoUrl.isNullOrBlank().not())
            ivPlayMask.visibleOrGone(tutorial.videoUrl.isNullOrBlank().not())
        }
    }

    private fun showEmbeddedVideoPlayer(videoUrl: String, videoCoverUrl: String?) {
        if (videoUrl.isBlank()) {
            Toaster.show(getString(R.string.video_not_available))
            return
        }
        if (isVideoPlayerShown) {
            return
        }
        isVideoPlayerShown = true

        binding.ivCover.gone()
        binding.ivPlayMask.gone()
        binding.btnPlayVideo.gone()
        binding.fcvVideoPlayer.visible()

        val tag = TAG_VIDEO_FRAGMENT
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.fcv_video_player,
                    TutorialVideoFragment.newInstance(videoUrl, videoCoverUrl),
                    tag,
                )
            }
        }
    }

    companion object {
        private const val TAG_VIDEO_FRAGMENT = "tutorial_video_fragment"
        private const val COVER_HEIGHT_ANIM_DURATION_MS = 300L
        private const val CONTENT_BOTTOM_PADDING_DP = 24

        fun start(context: Context, tutorial: TutorialResp) {
            context.startActivity(
                Intent(context, TutorialDetailActivity::class.java).apply {
                    putExtra(Constants.RouterKeys.TUTORIAL, tutorial)
                }
            )
        }
    }
}
