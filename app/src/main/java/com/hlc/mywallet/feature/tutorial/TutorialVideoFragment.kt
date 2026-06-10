package com.hlc.mywallet.feature.tutorial

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.load
import com.hlc.lib_base.extension.loadRounded
import com.hlc.mywallet.R
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.databinding.LayoutTutorialVideoPlayerBinding
import com.hlc.mywallet.feature.tutorial.player.TutorialVideoPlayerController

/**
 * 教程视频播放 Fragment，嵌入详情页封面区域。
 */
class TutorialVideoFragment : BaseVbFragment<LayoutTutorialVideoPlayerBinding>() {

    private var playerController: TutorialVideoPlayerController? = null

    override fun initView() {
        val videoUrl = arguments?.getString(Constants.RouterKeys.VIDEO_URL).orEmpty()
        val videoCoverUrl = arguments?.getString(Constants.RouterKeys.VIDEO_COVER_URL)
        binding.ivVideoCover.load(videoCoverUrl ?: R.drawable.ic_logo_big, scaleType = ImageView.ScaleType.CENTER_INSIDE)
        val aspectListener = activity as? OnVideoAspectRatioListener
        playerController = TutorialVideoPlayerController(
            binding = binding,
            context = requireContext(),
            lifecycleOwner = viewLifecycleOwner,
            videoUrl = videoUrl,
            adjustContainerAspectExternally = aspectListener != null,
        ).also { controller ->
            controller.onVideoAspectRatioReady = { aspectRatio ->
                binding.ivVideoCover.gone()
                aspectListener?.onVideoAspectRatioReady(aspectRatio)
            }
            controller.bind()
        }
    }

    interface OnVideoAspectRatioListener {
        fun onVideoAspectRatioReady(aspectRatio: Float)
    }

    override fun getPageStateContainer(view: View): ViewGroup? = null

    override fun onDestroyView() {
        playerController?.release()
        playerController = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(videoUrl: String,videoCoverUrl: String?): TutorialVideoFragment {
            return TutorialVideoFragment().apply {
                arguments = bundleOf(Constants.RouterKeys.VIDEO_URL to videoUrl,
                    Constants.RouterKeys.VIDEO_COVER_URL to videoCoverUrl)
            }
        }
    }
}
