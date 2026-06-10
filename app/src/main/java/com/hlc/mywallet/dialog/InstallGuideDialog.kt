package com.hlc.mywallet.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hlc.lib_base.BaseBottomSheetDialog
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.load
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.data.model.resp.InstallGuide
import com.hlc.mywallet.databinding.DialogInstallGuideBinding
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

/**
 * 安装引导弹窗，使用 Banner 轮播 InstallGuide.imageUrl
 */
class InstallGuideDialog : BaseBottomSheetDialog<DialogInstallGuideBinding>() {

    private var guideList: List<InstallGuide>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guideList = arguments?.getParcelableArrayList(KEY_GUIDE_LIST) ?: emptyList()
    }

    override fun initView() {
        binding.ivClose.onClick { dismiss() }
        if (guideList?.isEmpty() == true) {
            binding.banner.visibility = View.GONE
            return
        }
        binding.banner.apply {
            indicator = CircleIndicator(requireContext())
            setAdapter(object : BannerAdapter<InstallGuide, BannerImageHolder>(guideList) {
                override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerImageHolder {
                    val imageView = ImageView(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    return BannerImageHolder(imageView)
                }

                override fun onBindView(holder: BannerImageHolder, data: InstallGuide, position: Int, size: Int) {
                    holder.imageView.load(data.imageUrl, scaleType = ImageView.ScaleType.CENTER_INSIDE, placeholder = 0, error = 0)
                }
            })
        }
    }

    override fun getMaxHeight(): Int {
        return 500.dp
    }

    companion object {
        private const val KEY_GUIDE_LIST = "guide_list"

        fun newInstance(guideList: List<InstallGuide>): InstallGuideDialog {
            return InstallGuideDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(KEY_GUIDE_LIST, ArrayList(guideList))
                }
            }
        }
    }
}
