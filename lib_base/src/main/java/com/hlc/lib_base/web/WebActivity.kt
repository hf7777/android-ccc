package com.hlc.lib_base.web

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebView
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.R
import com.hlc.lib_base.databinding.ActivityWebBinding
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.visible
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient

/**
 * Universal WebView Activity
 */
class WebActivity : BaseVbActivity<ActivityWebBinding>() {

    private var agentWeb: AgentWeb? = null
    private var url: String = ""
    private var title: String = ""

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarDarkIcon(true)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            fitsSystemWindows(true)
        }
    }

    override fun initView() {
        url = intent.getStringExtra(EXTRA_URL) ?: ""
        title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        
        if (url.isEmpty()) {
            Toaster.show("URL cannot be empty")
            finish()
            return
        }

        setupTitleBar()
        setupWebView()
    }

    private fun setupTitleBar() {
        binding.titleBar.apply {
            setTitle(title.ifEmpty { getString(R.string.loading) })
            setOnBackClickListener { onBackPressed() }
        }
    }

    private fun setupWebView() {
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.webContainer, ViewGroup.LayoutParams(-1, -1))
            .useDefaultIndicator(resources.getColor(R.color.theme, null), 2)
            .setWebChromeClient(object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, receivedTitle: String?) {
                    super.onReceivedTitle(view, receivedTitle)
                    if (title.isEmpty() && !receivedTitle.isNullOrEmpty()) {
                        binding.titleBar.setTitle(receivedTitle)
                    }
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.progressBar.apply {
                        if (newProgress == 100) gone() else visible()
                    }
                }
            })
            .setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.gone()
                }
            })
            .setMainFrameErrorView(R.layout.layout_web_error, R.id.btn_reload)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            .interceptUnkownUrl()
            .createAgentWeb()
            .ready()
            .go(url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (agentWeb?.handleKeyEvent(keyCode, event) == true) {
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onPause() {
        agentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onResume() {
        agentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_TITLE = "extra_title"

        /**
         * Start WebActivity
         * @param context Context
         * @param url Web page URL
         * @param title Title (optional, uses web page title if empty)
         */
        fun start(context: Context, url: String, title: String = "") {
            val intent = Intent(context, WebActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
    }
}
