package com.hlc.mywallet.feature.tutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.databinding.ActivityTutorialVideoBinding

class TutorialVideoActivity : BaseVbActivity<ActivityTutorialVideoBinding>() {

    private var videoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        videoUrl = intent.getStringExtra(Constants.RouterKeys.VIDEO_URL)
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

    override fun initView() {
        val url = videoUrl
        if (url.isNullOrBlank()) {
            Toaster.show(getString(R.string.video_not_available))
            finish()
            return
        }

        if (supportFragmentManager.findFragmentById(R.id.fcv_video_player) == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.fcv_video_player,
                    TutorialVideoFragment.newInstance(url, null),
                    TAG_VIDEO_FRAGMENT
                )
            }
        }

        binding.ivFinish.onClick {
            finish()
        }
        applyImmersiveInsets()
    }

    private fun applyImmersiveInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBarInset = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val finishParams = binding.ivFinish.layoutParams as ConstraintLayout.LayoutParams
            finishParams.topMargin = 15.dp + statusBarInset.top
            finishParams.marginStart = 15.dp + statusBarInset.left
            binding.ivFinish.layoutParams = finishParams
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    companion object {
        private const val TAG_VIDEO_FRAGMENT = "tutorial_video_fragment"

        fun start(context: Context, videoUrl: String) {
            context.startActivity(
                Intent(context, TutorialVideoActivity::class.java).apply {
                    putExtra(Constants.RouterKeys.VIDEO_URL, videoUrl)
                }
            )
        }
    }
}
