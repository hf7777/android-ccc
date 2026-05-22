package com.hlc.mywallet.feature.deposit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ColorUtils
import com.hjq.toast.Toaster
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.getRouterString
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showConfirmDialog
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.common.ImageUploadCompressor
import com.hlc.mywallet.common.PermissionRequester
import com.hlc.mywallet.data.model.resp.InrDetailResp
import com.hlc.mywallet.databinding.ActivityPaymentDetailBinding
import com.hlc.mywallet.dialog.UploadProofDialog
import com.hlc.mywallet.feature.deposit.bean.DepositStatus
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentDetailActivity : BaseVbActivity<ActivityPaymentDetailBinding>() {

    private val viewModel: DepositViewModel by viewModels()

    private val grabRecordId: String by lazy {
        intent.getRouterString(Constants.RouterKeys.GRAB_RECORD_ID)
    }
    private var countDownTimer: CountDownTimer? = null

    private var needShowPageLoading: Boolean = true

    private var detail: InrDetailResp? = null

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

    override fun getBaseTitleBarTitle(): String = getString(R.string.payment_detail)

    override fun initView() {
        binding.btnCancel.onClick {
            showConfirmDialog(content = getString(R.string.are_you_sure_you_want_to_cancel_this_order)) {
                viewModel.cancelInrOrder(grabRecordId)
            }
        }
        binding.btnGoPay.onClick {
            handleGoPayClick()
        }
        binding.btnPaid.onClick {
            showUploadProofDialog()
        }
        setupCopyActions()
    }

    override fun initData() {
        if (grabRecordId.isEmpty()) {
            showPageError(message = getString(R.string.request_failed))
            return
        }
        needShowPageLoading = true
        showPageLoading()
        viewModel.getInrDetail(grabRecordId)
    }


    override fun observeData() {
        viewModel.inrDetailFlow.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                if (needShowPageLoading) {
                    showPageLoading()
                }
            },
            onSuccess = { detail ->
                hideLoading()
                showPageContent()
                bindDetail(detail)
            },
            onError = { _ ->
                hideLoading()
                showPageError(
                    onActionClick = {
                        if (grabRecordId.isNotEmpty()) {
                            needShowPageLoading = true
                            showPageLoading()
                            viewModel.getInrDetail(grabRecordId)
                        }
                    }
                )
            }
        )

        viewModel.cancelInrOrderFlow.collectWithError(
            lifecycleOwner = this,
            onLoading = { showLoading() },
            onSuccess = {
                AppEventBus.post(AppEvent.OrderInrListRefreshRequested)
                needShowPageLoading = false
                viewModel.getInrDetail(grabRecordId)
            },
            onError = { _ ->
                hideLoading()
            }
        )

        observeUploadImageFlow()

        viewModel.inrDepositConfirmFlow.collectWithError(
            lifecycleOwner = this,
            onLoading = { showLoading() },
            onSuccess = {
                AppEventBus.post(AppEvent.OrderInrListRefreshRequested)
                currentUploadProofDialog()?.dismissAllowingStateLoss()
                needShowPageLoading = false
                viewModel.getInrDetail(grabRecordId)
            },
            onError = { _ ->
                hideLoading()
            }
        )
    }

    private fun observeUploadImageFlow() {
        // 选择图片会短暂离开当前页面，常驻收集可以避免 SharedFlow 在这段生命周期切换中丢结果。
        lifecycleScope.launch {
            viewModel.uploadImageFlow.collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        showLoading()
                    }

                    is ApiResult.Success -> {
                        hideLoading()
                        currentUploadProofDialog()?.onImageUploadSuccess(result.data)
                    }

                    is ApiResult.Error -> {
                        hideLoading()
                        currentUploadProofDialog()?.onImageUploadFailed()
                        Toaster.show(result.exception.message ?: getString(R.string.error_network))
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onDestroy()
    }

    private fun bindDetail(detail: InrDetailResp) {
        this.detail = detail
        binding.tvStatus.text = detail.grabStatus
        binding.tvId.text = detail.grabId
        binding.tvOrderNo.text = detail.platformOrderNo
        binding.tvPaymentName.text = detail.channelCode
        binding.tvPaymentUpi.text = detail.upiId
        binding.tvPaymentAmount.text = "${detail.orderAmount.formatNumber()}Rs"
        binding.tvBeneficiaryName.text = detail.beneName
        binding.tvIfsc.text = detail.ifsc
        binding.tvAccount.text = detail.accountNo
        binding.tvMessage.text = detail.tradeCode

        if (detail.grabStatus == DepositStatus.GRAB.state) {
            binding.btnCancel.visible()
            binding.clRemaining.visible()
            startCountDown(detail.remainingSeconds ?: 0)
        } else {
            binding.btnCancel.gone()
            binding.clRemaining.gone()
            countDownTimer?.cancel()
        }

        binding.groupPaymentTool.visibleOrGone(detail.grabStatus != DepositStatus.CANCEL.state)

        binding.groupIfsc.visibleOrGone(!detail.ifsc.isNullOrEmpty())
    }

    private fun showUploadProofDialog() {
        if (detail == null) {
            Toaster.show(getString(R.string.request_failed))
            return
        }
        if (currentUploadProofDialog() != null) {
            return
        }
        UploadProofDialog.newInstance()
            .setOnImageSelectedListener { uri ->
                uploadSelectedProofImage(uri)
            }
            .setOnSubmitListener { utr, voucherUrl ->
                val currentDetail = detail ?: run {
                    Toaster.show(getString(R.string.request_failed))
                    return@setOnSubmitListener
                }
                val platformOrderNo = currentDetail.platformOrderNo.orEmpty()
                val grabId = currentDetail.grabId.orEmpty()
                if (platformOrderNo.isEmpty() || grabId.isEmpty()) {
                    Toaster.show(getString(R.string.request_failed))
                    return@setOnSubmitListener
                }
                viewModel.inrDepositConfirm(platformOrderNo, grabId, utr, voucherUrl)
            }
            .show(supportFragmentManager, UploadProofDialog::class.java.simpleName)
    }

    private fun currentUploadProofDialog(): UploadProofDialog? {
        return supportFragmentManager.findFragmentByTag(
            UploadProofDialog::class.java.simpleName
        ) as? UploadProofDialog
    }

    private fun uploadSelectedProofImage(uri: Uri) {
        showLoading()
        lifecycleScope.launch {
            val imagePart = ImageUploadCompressor.createMultipartPart(
                context = this@PaymentDetailActivity,
                uri = uri,
                partName = FILE_PART_NAME
            )
            if (imagePart == null) {
                hideLoading()
                currentUploadProofDialog()?.onImageUploadFailed()
                Toaster.show(getString(R.string.image_prepare_failed))
                return@launch
            }
            viewModel.uploadImage(imagePart)
        }
    }

    private fun handleGoPayClick() {
        val currentDetail = detail ?: run {
            Toaster.show(getString(R.string.request_failed))
            return
        }
        val launchIntent = resolvePaymentLaunchIntent(currentDetail) ?: return
        if (PermissionRequester.hasOverlayPermission(this)) {
            showOverlayAndLaunchApp(currentDetail, launchIntent)
            return
        }
        requestOverlayPermissionAndLaunch(currentDetail, launchIntent)
    }

    /**
     * 支付场景里悬浮窗权限只是增强体验，不应该阻塞用户继续拉起支付 App。
     * 所以无论权限页最终是否授权，都会继续执行跳转；只有授权成功时才展示悬浮窗。
     */
    private fun requestOverlayPermissionAndLaunch(detail: InrDetailResp, launchIntent: Intent) {
        PermissionRequester.requestOverlayPermission(this) { granted ->
            if (isFinishing || isDestroyed) {
                return@requestOverlayPermission
            }
            if (granted) {
                PaymentReferenceOverlayService.show(this, detail)
            }
            startActivity(launchIntent)
        }
    }

    private fun resolvePaymentLaunchIntent(detail: InrDetailResp): Intent? {
        val packageName = when {
            detail.channelCode.equals(CHANNEL_FREECHARGE, ignoreCase = true) -> PACKAGE_FREECHARGE
            detail.channelCode.equals(CHANNEL_MOBIKWIK, ignoreCase = true) -> PACKAGE_MOBIKWIK
            else -> null
        }
        if (packageName.isNullOrEmpty()) {
            Toaster.show(getString(R.string.payment_tool_not_supported))
            return null
        }
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        if (launchIntent == null) {
            val appName = detail.channelName?.takeIf { it.isNotBlank() } ?: detail.channelCode ?: packageName
            Toaster.show(getString(R.string.payment_app_not_installed, appName))
            return null
        }
        return launchIntent
    }

    private fun showOverlayAndLaunchApp(detail: InrDetailResp, launchIntent: Intent) {
        PaymentReferenceOverlayService.show(this, detail)
        startActivity(launchIntent)
    }

    private fun setupCopyActions() {
        binding.ivCopyId.onClick { copyText(binding.tvId.text?.toString()) }
        binding.ivCopyOrderNo.onClick { copyText(binding.tvOrderNo.text?.toString()) }
        binding.ivCopyPaymentAmount.onClick { copyText(binding.tvPaymentAmount.text?.toString()) }
        binding.ivCopyIfsc.onClick { copyText(binding.tvIfsc.text?.toString()) }
        binding.ivCopyAccount.onClick { copyText(binding.tvAccount.text?.toString()) }
        binding.ivCopyMessage.onClick { copyText(binding.tvMessage.text?.toString()) }
    }

    private fun copyText(text: String?) {
        if (text.isNullOrEmpty()) return
        ClipboardUtils.copyText(text)
        Toaster.show(getString(R.string.copy_success))
    }

    private fun startCountDown(remainingSeconds: Int) {
        countDownTimer?.cancel()
        if (remainingSeconds <= 0) {
            binding.tvTime.text = "00:00"
            return
        }
        countDownTimer = object : CountDownTimer(remainingSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTime.text = formatDuration((millisUntilFinished / 1000L).toInt())
            }

            override fun onFinish() {
                binding.tvTime.text = "00:00"
                needShowPageLoading = false
                viewModel.getInrDetail(grabRecordId)
            }
        }.start()
    }

    private fun formatDuration(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    companion object {
        private const val CHANNEL_FREECHARGE = "freecharge"
        private const val CHANNEL_MOBIKWIK = "mobikwik"
        private const val PACKAGE_FREECHARGE = "com.freecharge.android"
        private const val PACKAGE_MOBIKWIK = "com.mobikwik_new"
        private const val FILE_PART_NAME = "file"

        fun start(context: Context, grabRecordId: String) {
            Router.navigation(Routes.PAYMENT_DETAIL)
                .with(Constants.RouterKeys.GRAB_RECORD_ID, grabRecordId)
                .navigation(context)
        }
    }
}
