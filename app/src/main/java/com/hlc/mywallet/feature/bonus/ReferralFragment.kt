package com.hlc.mywallet.feature.bonus

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.ReferralAdapter
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.ReferralResp
import com.hlc.mywallet.databinding.FragmentReferralBinding
import com.hlc.mywallet.router.Routes
import com.hlc.lib_base.BaseLazyFragment
import com.hlc.lib_base.extension.setDrawablePadding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Wade
 * @since 2026/5/18
 */
@AndroidEntryPoint
class ReferralFragment : BaseLazyFragment<FragmentReferralBinding>() {

    private val viewModel: BonusViewModel by activityViewModels()

    private var taskCode: String? = null

    private val referralAdapter by lazy {
        ReferralAdapter(
            onClaimClick = { subordinate ->
                val currentTaskCode = taskCode?.takeIf { it.isNotBlank() } ?: return@ReferralAdapter
                val targetId = subordinate.subordinateAntId?.takeIf { it.isNotBlank() } ?: return@ReferralAdapter
                viewModel.claimReferralBonus(currentTaskCode, targetId)
            }
        )
    }

    override fun initView() {
        showPageLoading()
        binding.btnClaim.onClick {
            Router.navigation(Routes.BILLS)
                .with(Constants.RouterKeys.DEFAULT_TO_ACTIVITY, true)
                .navigation(this@ReferralFragment)
        }
        binding.tvAmount.setDrawablePadding(
            leftResId = R.drawable.ic_coin,
            leftPadding = 5.dp,
            drawableWidth = 18.dp,
            drawableHeight = 18.dp
        )
        binding.rvReferral.apply {
            layoutManager = object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean = false
            }
            adapter = referralAdapter
            isNestedScrollingEnabled = false
            addItemDecoration(
                SpaceItemDecoration.Builder()
                    .dividerColor(resources.getColor(R.color.home_tutorial_divider, null), 1.dp)
                    .build()
            )
        }
    }

    override fun loadLazyData() {
        viewModel.getReferralList()
    }

    override fun observeData() {
        viewModel.referralListState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showPageLoading()
            },
            onSuccess = { data ->
                showPageContent()
                renderReferral(data)
            },
            onError = {
                showPageError(onActionClick = {
                    showPageLoading()
                    viewModel.getReferralList()
                })
            }
        )

        viewModel.claimReferralBonusFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = {
                viewModel.getReferralList()
            },
            onError = {
                hideLoading()
            }
        )
    }

    private fun renderReferral(data: ReferralResp) {
        taskCode = data.taskCode
        binding.tvAmount.text = data.rewardAmount?.toString().formatNumber()
        referralAdapter.submitList(data.subordinates.orEmpty())
    }

    companion object {
        fun newInstance() = ReferralFragment()
    }
}
