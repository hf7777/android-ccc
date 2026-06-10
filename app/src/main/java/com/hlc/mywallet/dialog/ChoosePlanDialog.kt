package com.hlc.mywallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.loadRounded
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.DialogChoosePlanBinding

class ChoosePlanDialog : DialogFragment() {

    private var _binding: DialogChoosePlanBinding? = null
    private val binding: DialogChoosePlanBinding get() = _binding!!

    private var selectedPlan: Plan? = null
    private var onConfirmPlanListener: ((Plan) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.apply {
                setBackgroundDrawableResource(R.color.transparent)
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.CENTER)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChoosePlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlanImages()
        setupSelection()
        updateSelectionUi()

        binding.btnConfirm.onClick {
            selectedPlan?.let { plan ->
                onConfirmPlanListener?.invoke(plan)
            }
            dismiss()
        }
    }

    private fun setupPlanImages() {
        binding.ivPlanA.loadRounded(
            R.drawable.plan_a,
            radius = 4.dp,
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        )
        binding.ivPlanB.loadRounded(
            R.drawable.plan_b,
            radius = 4.dp,
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        )
    }

    private fun setupSelection() {
        val selectPlanA = { selectPlan(Plan.A) }
        val selectPlanB = { selectPlan(Plan.B) }

        binding.ivPlanA.onClick(action = selectPlanA)
        binding.rbPlanA.onClick(action = selectPlanA)
        binding.layoutPlanA.onClick(action = selectPlanA)

        binding.ivPlanB.onClick(action = selectPlanB)
        binding.rbPlanB.onClick(action = selectPlanB)
        binding.layoutPlanB.onClick(action = selectPlanB)
    }

    private fun selectPlan(plan: Plan) {
        selectedPlan = plan
        updateSelectionUi()
    }

    private fun updateSelectionUi() {
        val plan = selectedPlan
        val isPlanASelected = plan == Plan.A
        val isPlanBSelected = plan == Plan.B

        binding.rbPlanA.isChecked = isPlanASelected
        binding.rbPlanB.isChecked = isPlanBSelected
        binding.btnConfirm.isEnabled = plan != null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class Plan {
        A, B
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO = 0.95f

        fun newInstance(): ChoosePlanDialog = ChoosePlanDialog()
    }

    fun setOnConfirmListener(listener: (Plan) -> Unit): ChoosePlanDialog {
        onConfirmPlanListener = listener
        return this
    }
}
