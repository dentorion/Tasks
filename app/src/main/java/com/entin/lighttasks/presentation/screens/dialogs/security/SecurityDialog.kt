package com.entin.lighttasks.presentation.screens.dialogs.security

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SecurityDialogBinding
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.SUCCESS_CHECK_PASSWORD
import com.entin.lighttasks.presentation.util.SUCCESS_CREATE_PASSWORD
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SecurityDialog : DialogFragment() {

    private var _binding: SecurityDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SecurityDialogViewModel by viewModels()

    private var type: SecurityType? = null

    private fun setDialogWidth(width: Double) {
        val newWidth = (resources.displayMetrics.widthPixels * width).toInt()
        dialog?.window?.setLayout(newWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun hideSystemUI() {
        dialog?.let { dialog ->
            dialog.window?.let { window ->
                view?.let { view ->
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, view).systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.window?.apply {
                attributes?.windowAnimations = R.style.NoPaddingDialogTheme
                setGravity(Gravity.CENTER)
            }
            setDialogWidth(if (isOrientationLandscape(context)) LANDSCAPE_MODE else FULL_SCREEN)
        }
    }

    fun newInstance(
        type: SecurityType,
    ): SecurityDialog =
        SecurityDialog().apply {
            this.type = type
            this.arguments = bundleOf(ARG_SECURITY_TYPE to type)
        }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ARG_SECURITY_TYPE, type)
        super.onSaveInstanceState(bundleOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (savedInstanceState != null) {
            val securityType = arguments?.getParcelable<SecurityType>(ARG_SECURITY_TYPE)
            this.type = securityType
        }

        isCancelable = true
        _binding = SecurityDialogBinding.inflate(inflater, container, false)

        stateObserver()

        setupButtons()

        return binding.root
    }

    private fun stateObserver() {
        viewModel.action.observe(viewLifecycleOwner) { state: SecurityStateContract ->
            when(state) {
                /** Create password */
                SecurityStateContract.ErrorOnRepeatPassword -> {
                    binding.securityDialogLabel.text = getString(R.string.passwords_are_not_matching)
                    binding.securityDialogPassword.text = EMPTY_STRING
                }
                SecurityStateContract.RepeatPassword -> {
                    binding.securityDialogLabel.text = getString(R.string.repeat_password)
                    binding.securityDialogPassword.text = EMPTY_STRING
                }
                SecurityStateContract.SuccessOnRepeatPassword -> {
                    requireParentFragment().setFragmentResult(
                        SUCCESS_CREATE_PASSWORD,
                        bundleOf(SUCCESS_CREATE_PASSWORD to type)
                    )
                    dismiss()
                }
                /** Check password */
                SecurityStateContract.ErrorOnCheckPassword -> {
                    binding.securityDialogLabel.text = getString(R.string.wrong_password)
                    binding.securityDialogPassword.text = EMPTY_STRING
                }
                SecurityStateContract.SuccessOnCheckPassword -> {
                    requireParentFragment().setFragmentResult(
                        SUCCESS_CHECK_PASSWORD,
                        bundleOf(SUCCESS_CHECK_PASSWORD to type)
                    )
                    dismiss()
                }
                /** Can't found security item */
                SecurityStateContract.NotFoundSecurityItem -> dismiss()
            }
        }
    }

    private fun setupButtons() {
        with(binding) {
            securityDialogClear.setOnClickListener {
                if (binding.securityDialogPassword.text.isNotEmpty()) {
                    clearInputCode()
                } else {
                    dismiss()
                }
            }
            securityDialogNumber1.setOnClickListener { addSymbol(1) }
            securityDialogNumber2.setOnClickListener { addSymbol(2) }
            securityDialogNumber3.setOnClickListener { addSymbol(3) }
            securityDialogNumber4.setOnClickListener { addSymbol(4) }
            securityDialogNumber5.setOnClickListener { addSymbol(5) }
            securityDialogNumber6.setOnClickListener { addSymbol(6) }
            securityDialogNumber7.setOnClickListener { addSymbol(7) }
            securityDialogNumber8.setOnClickListener { addSymbol(8) }
            securityDialogNumber9.setOnClickListener { addSymbol(9) }
            securityDialogNumber0.setOnClickListener { addSymbol(0) }
            securityDialogSubmit.setOnClickListener {
                val insertedPassword = binding.securityDialogPassword.text.toString()
                type?.let { securityType ->
                    if (insertedPassword.isNotEmpty() && insertedPassword.isNotBlank() && insertedPassword.length >= 4) {
                        viewModel.onSubmitClicked(securityType, insertedPassword)
                    } else {
                        binding.securityDialogLabel.text = getString(R.string.minimum_4_characters)
                    }
                }
            }
        }
    }

    private fun addSymbol(symbol: Int) {
        binding.apply {
            if (securityDialogPassword.text.length < 8) {
                val new = securityDialogPassword.text.toString() + symbol.toString()
                securityDialogPassword.text = new
            }
        }
    }

    private fun clearInputCode() {
        binding.securityDialogPassword.text = EMPTY_STRING
    }

    override fun onDestroyView() {
        viewModel.insertedPassword = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.92

        const val ARG_SECURITY_TYPE = "ARG_SECURITY_TYPE"
        const val ARG_ON_SUCCESS = "ARG_ON_SUCCESS"
        const val ARG_SECURITY_ITEM_ID = "ARG_SECURITY_ITEM_ID"
    }
}
