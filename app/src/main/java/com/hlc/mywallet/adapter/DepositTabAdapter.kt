package com.hlc.mywallet.adapter

import android.content.Context
import android.graphics.Color
import com.blankj.utilcode.util.ColorUtils
import com.hlc.lib_base.extension.dp
import com.hlc.mywallet.R
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class DepositTabAdapter(
    private val titles: List<String>,
    private val onTabClick: (Int) -> Unit
) : CommonNavigatorAdapter() {

    override fun getCount(): Int = titles.size

    override fun getTitleView(context: Context, index: Int): IPagerTitleView {
        return ColorTransitionPagerTitleView(context).apply {
            normalColor = Color.GRAY
            selectedColor = ColorUtils.getColor(R.color.theme)
            text = titles[index]
            textSize = 15f
            setOnClickListener {
                onTabClick(index)
            }
        }
    }

    override fun getIndicator(context: Context): IPagerIndicator {
        return LinePagerIndicator(context).apply {
            mode = LinePagerIndicator.MODE_WRAP_CONTENT
            lineHeight = 3.dp.toFloat()
            lineWidth = 20.dp.toFloat()
            roundRadius = 2.dp.toFloat()
            startInterpolator = null
            endInterpolator = null
            setColors(context.resources.getColor(R.color.theme, null))
        }
    }
}
