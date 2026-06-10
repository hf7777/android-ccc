package com.hlc.mywallet.dialog

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hjq.toast.Toaster
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.mywallet.R
import com.hlc.mywallet.common.AppUpdateDownloader
import com.hlc.mywallet.data.model.resp.VersionResp
import com.hlc.mywallet.databinding.DialogUpdateBinding
import kotlinx.coroutines.launch

class UpdateDialog : DialogFragment() {

    private var _binding: DialogUpdateBinding? = null
    private val binding: DialogUpdateBinding get() = _binding!!

    private val version: VersionResp by lazy {
        requireNotNull(
            arguments?.let {
                BundleCompat.getParcelable(it, KEY_VERSION, VersionResp::class.java)
            }
        )
    }

    private val isForceUpdate: Boolean
        get() = version.forceUpdate.equals(FORCE_UPDATE_YES, ignoreCase = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startDownload()
        } else {
            // 无通知权限仍允许下载，仅无法展示通知栏进度
            startDownload()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(!isForceUpdate)
            window?.apply {
                setBackgroundDrawableResource(R.color.transparent)
                setGravity(Gravity.CENTER)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interceptBackPress()
        bindVersionInfo()
        setupButtons()
        observeDownloadState()
    }

    /** 拦截系统返回键与返回手势，避免弹窗被关闭 */
    private fun interceptBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = Unit
            }
        )
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth = (resources.displayMetrics.widthPixels - 56.dp).coerceAtMost(360.dp)
        dialog?.window?.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun bindVersionInfo() {
        binding.tvContent.text = getString(
            R.string.app_update_content,
            version.versionName,
            version.versionCode
        )
        binding.btnLater.visibleOrGone(!isForceUpdate)
        binding.spaceFooter.visibleOrGone(!isForceUpdate)
        renderPrimaryState(AppUpdateDownloader.state.value)
    }

    private fun setupButtons() {
        binding.btnLater.onClick {
            AppUpdateDownloader.cancel()
            dismissAllowingStateLoss()
        }
        binding.btnPrimary.onClick {
            when (val state = AppUpdateDownloader.state.value) {
                is AppUpdateDownloader.DownloadState.Success -> {
                    AppUpdateDownloader.installApk(requireContext())
                }
                is AppUpdateDownloader.DownloadState.Downloading -> Unit
                else -> requestDownload()
            }
        }
    }

    private fun requestDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                startDownload()
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startDownload()
        }
    }

    private fun startDownload() {
        if (version.apkUrl.isBlank()) {
            Toaster.show(getString(R.string.app_update_invalid_url))
            return
        }
        binding.btnPrimary.isEnabled = false
        binding.progressBar.visible()
        binding.tvProgress.visible()
        AppUpdateDownloader.startDownload(
            requireContext(),
            version.apkUrl,
            version.versionName
        )
    }

    private fun observeDownloadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppUpdateDownloader.state.collect { state ->
                    renderPrimaryState(state)
                }
            }
        }
    }

    private fun renderPrimaryState(state: AppUpdateDownloader.DownloadState) {
        when (state) {
            is AppUpdateDownloader.DownloadState.Idle -> {
                binding.progressBar.gone()
                binding.tvProgress.gone()
                binding.btnPrimary.isEnabled = true
                binding.btnPrimary.text = getString(R.string.update_now)
            }
            is AppUpdateDownloader.DownloadState.Downloading -> {
                binding.progressBar.visible()
                binding.tvProgress.visible()
                binding.progressBar.progress = state.progress
                binding.tvProgress.text = getString(R.string.app_update_progress, state.progress)
                binding.btnPrimary.isEnabled = false
                binding.btnPrimary.text = getString(R.string.update_now)
            }
            is AppUpdateDownloader.DownloadState.Success -> {
                binding.progressBar.progress = 100
                binding.tvProgress.text = getString(R.string.app_update_progress, 100)
                binding.btnPrimary.isEnabled = true
                binding.btnPrimary.text = getString(R.string.install)
            }
            is AppUpdateDownloader.DownloadState.Failed -> {
                binding.progressBar.gone()
                binding.tvProgress.gone()
                binding.btnPrimary.isEnabled = true
                binding.btnPrimary.text = getString(R.string.update_now)
                Toaster.show(state.message)
            }
        }
    }

    companion object {
        const val TAG = "UpdateDialog"
        private const val KEY_VERSION = "version"
        private const val FORCE_UPDATE_YES = "Y"

        fun newInstance(version: VersionResp): UpdateDialog {
            return UpdateDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_VERSION, version)
                }
            }
        }
    }
}
