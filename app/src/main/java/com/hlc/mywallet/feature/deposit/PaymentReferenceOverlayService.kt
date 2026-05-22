package com.hlc.mywallet.feature.deposit

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.blankj.utilcode.util.ClipboardUtils
import com.hjq.toast.Toaster
import com.hlc.lib_base.extension.dp
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.InrDetailResp
import com.hlc.mywallet.databinding.LayoutPaymentReferenceOverlayBinding
import kotlin.math.min

class PaymentReferenceOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayBinding: LayoutPaymentReferenceOverlayBinding? = null
    private var overlayLayoutParams: WindowManager.LayoutParams? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_HIDE -> stopSelf()
            ACTION_SHOW -> showOrUpdateOverlay(intent)
            else -> Unit
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showOrUpdateOverlay(intent: Intent) {
        val binding = overlayBinding ?: createOverlayView()
        val content = OverlayContent(
            beneficiary = intent.getStringExtra(EXTRA_BENEFICIARY),
            account = intent.getStringExtra(EXTRA_ACCOUNT),
            message = intent.getStringExtra(EXTRA_MESSAGE),
            ifsc = intent.getStringExtra(EXTRA_IFSC)
        )
        bindContent(binding, content)
    }

    private fun createOverlayView(): LayoutPaymentReferenceOverlayBinding {
        val binding = LayoutPaymentReferenceOverlayBinding.inflate(LayoutInflater.from(this))
        val displayWidth = resources.displayMetrics.widthPixels
        val overlayWidth = min(displayWidth - 24.dp, 200.dp)
        overlayLayoutParams = WindowManager.LayoutParams(
            overlayWidth,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (displayWidth - overlayWidth - 12.dp).coerceAtLeast(12.dp)
            y = 96.dp
        }

        setupActions(binding)
        windowManager?.addView(binding.root, overlayLayoutParams)
        overlayBinding = binding
        return binding
    }

    private fun setupActions(binding: LayoutPaymentReferenceOverlayBinding) {
        binding.ivClose.setOnClickListener { stopSelf() }
        binding.layoutDragHeader.setOnTouchListener(DragTouchListener())
        setupCopyAction(binding.rowBeneficiary, binding.tvBeneficiary)
        setupCopyAction(binding.ivCopyBeneficiary, binding.tvBeneficiary)
        setupCopyAction(binding.rowAccount, binding.tvAccount)
        setupCopyAction(binding.ivCopyAccount, binding.tvAccount)
        setupCopyAction(binding.rowMessage, binding.tvMessage)
        setupCopyAction(binding.ivCopyMessage, binding.tvMessage)
        setupCopyAction(binding.rowIfsc, binding.tvIfsc)
        setupCopyAction(binding.ivCopyIfsc, binding.tvIfsc)
    }

    private fun setupCopyAction(view: View, textView: TextView) {
        view.setOnClickListener {
            copyText(textView.text?.toString())
        }
    }

    private fun bindContent(
        binding: LayoutPaymentReferenceOverlayBinding,
        content: OverlayContent
    ) {
        binding.tvBeneficiary.text = content.beneficiary.toDisplayValue()
        binding.tvAccount.text = content.account.toDisplayValue()
        binding.tvMessage.text = content.message.toDisplayValue()
        val ifsc = content.ifsc.orEmpty()
        if (ifsc.isBlank()) {
            binding.rowIfsc.visibility = View.GONE
            binding.dividerIfsc.visibility = View.GONE
        } else {
            binding.rowIfsc.visibility = View.VISIBLE
            binding.dividerIfsc.visibility = View.VISIBLE
            binding.tvIfsc.text = ifsc
        }
    }

    private fun copyText(text: String?) {
        if (text.isNullOrBlank() || text == DASH) {
            return
        }
        ClipboardUtils.copyText(text)
        Toaster.show(getString(R.string.copy_success))
    }

    private fun removeOverlay() {
        val binding = overlayBinding ?: return
        runCatching {
            windowManager?.removeView(binding.root)
        }
        overlayBinding = null
        overlayLayoutParams = null
    }

    private fun overlayWindowType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun String?.toDisplayValue(): String {
        return this?.takeIf { it.isNotBlank() } ?: DASH
    }

    private inner class DragTouchListener : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val params = overlayLayoutParams ?: return false
            val rootView = overlayBinding?.root ?: return false
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    val displayWidth = resources.displayMetrics.widthPixels
                    val displayHeight = resources.displayMetrics.heightPixels
                    val maxX = (displayWidth - rootView.width).coerceAtLeast(0)
                    val maxY = (displayHeight - rootView.height).coerceAtLeast(0)
                    params.x = (initialX + (event.rawX - initialTouchX).toInt()).coerceIn(0, maxX)
                    params.y = (initialY + (event.rawY - initialTouchY).toInt()).coerceIn(0, maxY)
                    windowManager?.updateViewLayout(rootView, params)
                    return true
                }
            }
            return false
        }
    }

    private data class OverlayContent(
        val beneficiary: String?,
        val account: String?,
        val message: String?,
        val ifsc: String?
    )

    companion object {
        private const val ACTION_SHOW = "payment_reference_overlay_show"
        private const val ACTION_HIDE = "payment_reference_overlay_hide"
        private const val EXTRA_BENEFICIARY = "extra_beneficiary"
        private const val EXTRA_ACCOUNT = "extra_account"
        private const val EXTRA_MESSAGE = "extra_message"
        private const val EXTRA_IFSC = "extra_ifsc"
        private const val DASH = "-"

        fun show(context: Context, detail: InrDetailResp) {
            context.applicationContext.startService(
                Intent(context.applicationContext, PaymentReferenceOverlayService::class.java).apply {
                    action = ACTION_SHOW
                    putExtra(EXTRA_BENEFICIARY, detail.beneName)
                    putExtra(EXTRA_ACCOUNT, detail.accountNo)
                    putExtra(EXTRA_MESSAGE, detail.tradeCode)
                    putExtra(EXTRA_IFSC, detail.ifsc)
                }
            )
        }

        fun hide(context: Context) {
            context.applicationContext.startService(
                Intent(context.applicationContext, PaymentReferenceOverlayService::class.java).apply {
                    action = ACTION_HIDE
                }
            )
        }
    }
}
