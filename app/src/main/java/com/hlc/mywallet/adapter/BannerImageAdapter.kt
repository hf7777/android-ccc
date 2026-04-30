package com.hlc.mywallet.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hlc.mywallet.data.model.resp.BannersResp
import com.youth.banner.adapter.BannerAdapter

/**
 * Banner 图片适配器
 */
class BannerImageAdapter(
    data: List<BannersResp>
) : BannerAdapter<BannersResp, BannerImageAdapter.BannerViewHolder>(data) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return BannerViewHolder(imageView)
    }

    override fun onBindView(holder: BannerViewHolder, data: BannersResp, position: Int, size: Int) {
        Glide.with(holder.imageView)
            .load(data.imageUrl)
            .into(holder.imageView)
    }

    class BannerViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}