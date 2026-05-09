package com.hlc.mywallet.feature.deposit

import android.content.Context
import android.os.CountDownTimer
import androidx.activity.viewModels
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ColorUtils
import com.hjq.toast.Toaster
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.getRouterString
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.InrDetailResp
import com.hlc.mywallet.databinding.ActivityPaymentDetailBinding
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentDetailActivity : BaseVbActivity<ActivityPaymentDetailBinding>() {

    private val viewModel: DepositViewModel by viewModels()

    private val grabRecordId: String by lazy {
        intent.getRouterString(KEY_GRAB_RECORD_ID)
    }
    private var countDownTimer: CountDownTimer? = null

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    override fun initView() {
        binding.titleBar.setOnBackClickListener {
            finish()
        }
        binding.btnCancel.onClick {
            finish()
        }
        binding.btnGoPay.onClick {
            Toaster.show(getString(R.string._1_go_pay))
        }
        binding.btnPaid.onClick {
            Toaster.show(getString(R.string._2_paid))
        }
        setupCopyActions()
    }

    override fun initData() {
        if (grabRecordId.isEmpty()) {
            showPageError(message = getString(R.string.request_failed))
            return
        }
        viewModel.getInrDetail(grabRecordId)
    }

    override fun observeData() {
        viewModel.inrDetailFlow.collectWithError(
            lifecycleOwner = this,
            onLoading = { showPageLoading() },
            onSuccess = { detail ->
                showPageContent()
                bindDetail(detail)
            },
            onError = { errorMsg ->
                showPageError(
                    message = errorMsg,
                    onActionClick = {
                        if (grabRecordId.isNotEmpty()) {
                            viewModel.getInrDetail(grabRecordId)
                        }
                    }
                )
            }
        )
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onDestroy()
    }

    private fun bindDetail(detail: InrDetailResp) {
        binding.tvStatus.text = detail.grabStatus
        binding.tvId.text = detail.grabId
        binding.tvOrderNo.text = detail.platformOrderNo
        binding.tvPaymentName.text = detail.channelCode
        binding.tvPaymentUpi.text = detail.upiId
        binding.tvPaymentAmount.text = detail.orderAmount
        binding.tvBeneficiaryName.text = detail.beneName
        binding.tvBank.text = detail.tradeCode
        binding.tvIfsc.text = detail.ifsc
        binding.tvAccount.text = detail.accountNo
        binding.tvMessage.text = detail.paymentMessage
        binding.clRemaining.visibleOrGone(detail.remainingSeconds > 0)
        startCountDown(detail.remainingSeconds)
    }

    private fun setupCopyActions() {
        binding.ivCopyId.onClick { copyText(binding.tvId.text?.toString()) }
        binding.ivCopyOrderNo.onClick { copyText(binding.tvOrderNo.text?.toString()) }
        binding.ivCopyPaymentAmount.onClick { copyText(binding.tvPaymentAmount.text?.toString()) }
        binding.ivCopyBank.onClick { copyText(binding.tvBank.text?.toString()) }
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
            }
        }.start()
    }

    private fun formatDuration(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    companion object {
        private const val KEY_GRAB_RECORD_ID = "grab_record_id"

        fun start(context: Context, grabRecordId: String) {
            Router.navigation(Routes.PAYMENT_DETAIL)
                .with(KEY_GRAB_RECORD_ID, grabRecordId)
                .navigation(context)
        }
    }
}
