package com.hlc.mywallet.feature.bonus

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ColorUtils
import com.hlc.lib_base.BaseLazyFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.setDrawablePadding
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.NewbieAdapter
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.NewbieSummaryResp
import com.hlc.mywallet.data.model.resp.NewbieTaskResp
import com.hlc.mywallet.data.model.resp.TutorialResp
import com.hlc.mywallet.databinding.FragmentNewbieBinding
import com.hlc.mywallet.feature.tutorial.TutorialDetailActivity
import com.hlc.mywallet.router.Routes
import com.hlc.mywallet.storage.CacheKeys
import com.hlc.mywallet.storage.CacheStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * @author Wade
 * @since 2026/5/18
 */
@AndroidEntryPoint
class NewbieFragment : BaseLazyFragment<FragmentNewbieBinding>() {

    private val viewModel: BonusViewModel by activityViewModels()

    @Inject
    lateinit var cacheStorage: CacheStorage

    private var newbieSummary: NewbieSummaryResp? = null
    private var hasCompletedFirstPageLoad = false
    private var hasLoadedNewbieList = false
    private var hasLoadedNewbieSummary = false
    private var skipNextResumeRefresh = true

    private val newbieAdapter by lazy {
        NewbieAdapter(
            onTutorialClick = { task ->
                openTutorial(task)
            },
            onDoneClick = { task ->
                task.actionRoute?.let { route ->
                    navigation(route)
                }
            }
        )
    }

    override fun initView() {
        if (!hasCompletedFirstPageLoad) {
            showPageLoading()
        }
        binding.apply {
            btnClaim.visibleOrGone(false)
            btnClaim.onClick {
                newbieSummary?.let {
                    if (it.isRewarded == 0) {
                        viewModel.claimBonus(it.taskCode)
                    } else {
                        Router.navigation(Routes.BILLS)
                            .with(Constants.RouterKeys.DEFAULT_TO_ACTIVITY, true)
                            .navigation(this@NewbieFragment)
                    }
                }
            }
            tvAmount.setDrawablePadding(
                leftResId = R.drawable.ic_coin,
                leftPadding = 5.dp,
                drawableWidth = 18.dp,
                drawableHeight = 18.dp
            )
            rvNewbie.layoutManager = object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean = false
            }
            rvNewbie.adapter = newbieAdapter
            rvNewbie.isNestedScrollingEnabled = false
            rvNewbie.addItemDecoration(
                SpaceItemDecoration.Builder()
                    .dividerColor(resources.getColor(R.color.home_tutorial_divider, null), 1.dp)
                    .build()
            )
        }
    }

    override fun loadLazyData() {
        requestNewbieData()
    }

    override fun onResume() {
        super.onResume()
        if (skipNextResumeRefresh) {
            skipNextResumeRefresh = false
            return
        }
        if (hasLoadedLazyData()) {
            requestNewbieData()
        }
    }

    override fun observeData() {
        viewModel.newbieListState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { data ->
                newbieAdapter.submitList(buildDisplayTasks(data))
                onFirstPageSectionLoaded(isListLoaded = true)
            },
            onError = {
                onFirstPageLoadError()
            }
        )

        viewModel.newbieSummaryState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { data ->
                renderNewbieSummary(data)
                onFirstPageSectionLoaded(isSummaryLoaded = true)
            },
            onError = {
                onFirstPageLoadError()
            }
        )

        viewModel.claimBonusFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = {
                hideLoading()
                viewModel.getNewbieSummary()
            },
            onError = {
                hideLoading()
            }
        )
    }

    private fun renderNewbieSummary(data: NewbieSummaryResp) {
        this.newbieSummary = data
        binding.tvAmount.text = data.totalReward.toString().formatNumber()
        binding.btnClaim.visibleOrGone(data.isCompleted == 1)

        val colorRes = if (data.isRewarded == 0) {
            R.color.wallet_tips
        } else {
            R.color.theme
        }
        binding.btnClaim.text = if (data.isRewarded == 0) {
            getString(R.string.claim)
        } else {
            getString(R.string.claimed)
        }
        binding.btnClaim.shapeDrawableBuilder
            .setSolidColor(ColorUtils.getColor(colorRes))
            .intoBackground()
    }

    private fun openTutorial(task: NewbieTaskResp) {
        viewLifecycleOwner.lifecycleScope.launch {
            val tutorialId = task.tutorialId?.takeIf { it.isNotBlank() }
            val cachedTutorials = cacheStorage.getList(CacheKeys.TUTORIALS, TutorialResp::class.java).orEmpty()
            val tutorial = cachedTutorials.firstOrNull { it.id == tutorialId }

            if (tutorial != null) {
                TutorialDetailActivity.start(requireContext(), tutorial)
            } else {
                navigation(Routes.TUTORIAL_LIST)
            }
        }
    }

    private fun buildDisplayTasks(tasks: List<NewbieTaskResp>): List<NewbieTaskResp> {
        val taskMap = tasks.mapNotNull { task ->
            val taskCode = task.taskCode ?: return@mapNotNull null
            taskCode to task
        }.toMap()

        return TASK_CONFIGS.mapNotNull { (taskCode, config) ->
            val task = taskMap[taskCode] ?: return@mapNotNull null
            task.copy(
                taskName = task.taskName?.takeIf { it.isNotBlank() } ?: getString(config.titleRes)
            ).apply {
                img = config.imgRes
                actionRoute = config.actionRoute
                isDone = status == STATUS_DONE || status == STATUS_CLAIMED
            }
        }
    }

    /**
     * 首次进入页面时会同时请求 summary 和 task list。
     * 只有两个接口都成功后才切回内容态，避免页面过早露出半成品内容。
     */
    private fun requestNewbieData() {
        if (!hasCompletedFirstPageLoad) {
            hasLoadedNewbieList = false
            hasLoadedNewbieSummary = false
        }
        viewModel.getNewbieList()
        viewModel.getNewbieSummary()
    }

    private fun onFirstPageSectionLoaded(
        isListLoaded: Boolean = false,
        isSummaryLoaded: Boolean = false
    ) {
        if (hasCompletedFirstPageLoad) return

        if (isListLoaded) {
            hasLoadedNewbieList = true
        }
        if (isSummaryLoaded) {
            hasLoadedNewbieSummary = true
        }

        if (hasLoadedNewbieList && hasLoadedNewbieSummary) {
            hasCompletedFirstPageLoad = true
            showPageContent()
        }
    }

    private fun onFirstPageLoadError() {
        if (hasCompletedFirstPageLoad) return
        showPageError(onActionClick = {
            showPageLoading()
            requestNewbieData()
        })
    }

    companion object {
        private const val TASK_CODE_BIND_TELEGRAM = "bind_telegram"
        private const val TASK_CODE_SET_PIN = "set_pin"
        private const val TASK_CODE_ADD_TOOL = "add_tool"
        private const val TASK_CODE_COMPLETE_DEPOSIT = "complete_deposit"
        private const val STATUS_DONE = "done"
        private const val STATUS_CLAIMED = "claimed"

        private val TASK_CONFIGS = linkedMapOf(
            TASK_CODE_BIND_TELEGRAM to TaskUiConfig(
                imgRes = R.drawable.ic_bonus_robot,
                titleRes = R.string.bind_telegram_robot,
                actionRoute = Routes.BIND_TG
            ),
            TASK_CODE_SET_PIN to TaskUiConfig(
                imgRes = R.drawable.ic_bonus_set_pin,
                titleRes = R.string.set_pin,
                actionRoute = Routes.PIN
            ),
            TASK_CODE_ADD_TOOL to TaskUiConfig(
                imgRes = R.drawable.ic_bonus_add_tool,
                titleRes = R.string.add_tool,
                actionRoute = Routes.PAY_CHANNEL
            ),
            TASK_CODE_COMPLETE_DEPOSIT to TaskUiConfig(
                imgRes = R.drawable.ic_bonus_order,
                titleRes = R.string.complete_a_deposit_order,
                actionRoute = Routes.DEPOSIT
            )
        )

        fun newInstance() = NewbieFragment()
    }

    private data class TaskUiConfig(
        val imgRes: Int,
        val titleRes: Int,
        val actionRoute: String
    )
}
