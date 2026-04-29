package com.hlc.mywallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.WidgetBottomNavigationBinding

class BottomNavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: WidgetBottomNavigationBinding =
        WidgetBottomNavigationBinding.inflate(LayoutInflater.from(context), this, true)
    private var currentPosition = 0
    private var onTabSelectedListener: ((Int) -> Unit)? = null

    private val tabs = listOf(
        TabItem(R.drawable.ic_home, R.drawable.ic_home_selected, R.string.tab_home),
        TabItem(R.drawable.ic_home, R.drawable.ic_home_selected, R.string.tab_home),
        TabItem(R.drawable.ic_home, R.drawable.ic_home_selected, R.string.tab_home),
        TabItem(R.drawable.ic_home, R.drawable.ic_home_selected, R.string.tab_home),
        TabItem(R.drawable.ic_home, R.drawable.ic_home_selected, R.string.tab_home)
    )

    init {
        setupTabs()
    }

    private fun setupTabs() {
        binding.tab1.setOnClickListener { selectTab(0) }
        binding.tab2.setOnClickListener { selectTab(1) }
        binding.tab3.setOnClickListener { selectTab(2) }
        binding.tab4.setOnClickListener { selectTab(3) }
        binding.tab5.setOnClickListener { selectTab(4) }

        selectTab(0)
    }

    fun selectTab(position: Int) {
        if (position == currentPosition) return
        
        currentPosition = position
        updateTabStates()
        onTabSelectedListener?.invoke(position)
    }

    private fun updateTabStates() {
        val tabViews = listOf(
            Triple(binding.tab1, binding.tab1Icon, binding.tab1Text),
            Triple(binding.tab2, binding.tab2Icon, binding.tab2Text),
            Triple(binding.tab3, binding.tab3Icon, binding.tab3Text),
            Triple(binding.tab4, binding.tab4Icon, binding.tab4Text),
            Triple(binding.tab5, binding.tab5Icon, binding.tab5Text)
        )

        tabViews.forEachIndexed { index, (_, icon, text) ->
            val tab = tabs[index]
            val isSelected = index == currentPosition
            
            icon.setImageResource(if (isSelected) tab.iconSelected else tab.iconNormal)
            text.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelected) R.color.tab_selected else R.color.tab_normal
                )
            )
        }
    }

    fun setOnTabSelectedListener(listener: (Int) -> Unit) {
        onTabSelectedListener = listener
    }

    data class TabItem(
        val iconNormal: Int,
        val iconSelected: Int,
        val text: Int
    )
}
