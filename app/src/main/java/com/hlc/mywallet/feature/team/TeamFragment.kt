package com.hlc.mywallet.feature.team

import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.StringUtils
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.loadRounded
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.TeamStatisticsResp
import com.hlc.mywallet.databinding.FragmentTeamBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamFragment : BaseVbFragment<FragmentTeamBinding>() {

    private val viewModel: TeamViewModel by viewModels()

    private var teamStatistics: TeamStatisticsResp? = null

    override fun initView() {
        // 配置下拉刷新
        binding.refreshLayout.apply {
            setColorSchemeResources(R.color.theme)
            setOnRefreshListener {
                refreshData()
            }
        }

        binding.btnCopy.onClick {
            teamStatistics?.let {
                if (!it.inviteLink.isNullOrEmpty()) {
                    ClipboardUtils.copyText(it.inviteLink)
                    Toaster.show(getString(R.string.copy_success))
                }
            }
        }
        
        viewModel.getTeamStatistics()
    }

    private fun refreshData() {
        viewModel.getTeamStatistics()
    }

    override fun observeData() {
        viewModel.teamStatisticsState.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                if (!binding.refreshLayout.isRefreshing) {
                    showLoading()
                }
            },
            onSuccess = { data ->
                hideLoading()
                binding.refreshLayout.isRefreshing = false
                updateTeamStatistics(data)
            },
            onError = {
                hideLoading()
                binding.refreshLayout.isRefreshing = false
            }
        )
    }

    private fun updateTeamStatistics(data: TeamStatisticsResp) {
        teamStatistics = data
        binding.apply {
            tvTotalCommission.text = data.totalCommission ?: ""
            tvTodayDepositCount.text = data.todayDepositCount.toString()
            tvYesterdayDepositCount.text = data.yesterdayDepositCount.toString()
            tvTotalDepositCount.text = data.totalDepositCount.toString()
            tvTodayCommission.text = data.todayCommission ?: ""
            tvYesterdayCommission.text = data.yesterdayCommission ?: ""
            tvTotalSublineCount.text = data.totalSublineCount.toString()
            tvLink.text = data.inviteLink ?: ""

            // 构建佣金描述文本，高亮显示百分比
            val parentRate = data.parentCommissionRate ?: "0"
            val grandParentRate = data.grandparentCommissionRate ?: "0"
            val commissionText = StringUtils.getString(R.string.commission_desc, parentRate, grandParentRate)
            
            val parentRateText = "$parentRate%"
            val grandParentRateText = "$grandParentRate%"
            val highlightColor = ColorUtils.getColor(R.color.highlight_yellow)
            val parentStart = commissionText.indexOf(parentRateText)
            val grandParentStart = commissionText.indexOf(
                grandParentRateText,
                startIndex = (parentStart + parentRateText.length).coerceAtLeast(0)
            )

            if (parentStart >= 0 && grandParentStart >= 0) {
                SpanUtils.with(tvCommissionDesc)
                    .append(commissionText.substring(0, parentStart))
                    .append(parentRateText)
                    .setForegroundColor(highlightColor)
                    .append(commissionText.substring(parentStart + parentRateText.length, grandParentStart))
                    .append(grandParentRateText)
                    .setForegroundColor(highlightColor)
                    .append(commissionText.substring(grandParentStart + grandParentRateText.length))
                    .create()
            } else {
                tvCommissionDesc.text = commissionText
            }

            ivCover.loadRounded(data.commissionModelImageUrl)

            val result = 100000 * parentRate.toDouble() / 100 + 200000 * grandParentRate.toDouble() / 100
            tvCal.text = "100000*${parentRateText}+200000*${grandParentRateText}=${result.toString().formatNumber()}"
        }
    }

    companion object {
        fun newInstance() = TeamFragment()
    }
}
