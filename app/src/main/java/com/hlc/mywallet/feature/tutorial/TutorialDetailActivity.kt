package com.hlc.mywallet.feature.tutorial

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.text.HtmlCompat
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.loadRounded
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.mywallet.R
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.TutorialResp
import com.hlc.mywallet.databinding.ActivityTutorialDetailBinding

class TutorialDetailActivity : BaseVbActivity<ActivityTutorialDetailBinding>() {

    private var tutorial: TutorialResp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        tutorial = intent.getParcelableExtra(Constants.RouterKeys.TUTORIAL)
        super.onCreate(savedInstanceState)
    }

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    override fun useBaseTitleBar(): Boolean = true

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
        binding.btnPlayVideo.onClick {
            playVideoWithSystemPlayer(currentTutorial.videoUrl)
        }
        binding.ivCover.onClick {
            if (!currentTutorial.videoUrl.isNullOrBlank()) {
                playVideoWithSystemPlayer(currentTutorial.videoUrl)
            }
        }
    }

    private fun bindTutorial(tutorial: TutorialResp) {
        binding.apply {
            ivCover.loadRounded(tutorial.coverImage, 8)
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

    /**
     * 教程视频是服务端下发的直链地址，这里直接交给系统播放器处理，
     * 可以复用用户手机里已经安装的视频 App，避免应用内再维护一套播放能力。
     */
    private fun playVideoWithSystemPlayer(videoUrl: String?) {
        if (videoUrl.isNullOrBlank()) {
            Toaster.show(getString(R.string.video_not_available))
            return
        }

        val videoIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(videoUrl), "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        runCatching {
            startActivity(videoIntent)
        }.onFailure {
            Toaster.show(getString(R.string.video_player_not_found))
        }
    }

    companion object {
        fun start(context: Context, tutorial: TutorialResp) {
            context.startActivity(
                Intent(context, TutorialDetailActivity::class.java).apply {
                    putExtra(Constants.RouterKeys.TUTORIAL, tutorial)
                }
            )
        }
    }
}
