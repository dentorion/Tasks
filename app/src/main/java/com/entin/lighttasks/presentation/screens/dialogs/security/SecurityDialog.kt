package com.entin.lighttasks.presentation.screens.dialogs.security

import android.os.Bundle
import android.util.Log
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
import com.entin.lighttasks.presentation.util.BUNDLE_PASSWORD_RESULT_SECURITY_TYPE
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.SUCCESS_CHECK_PASSWORD_RESULT
import com.entin.lighttasks.presentation.util.SUCCESS_ADD_PASSWORD_RESULT
import com.entin.lighttasks.presentation.util.BUNDLE_IS_PASSWORD_CREATION
import com.entin.lighttasks.presentation.util.BUNDLE_PASSWORD_VALUE
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SecurityDialog : DialogFragment() {

    private var _binding: SecurityDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SecurityDialogViewModel by viewModels()

    private var security: Security? = null

    fun newInstance(
        type: Security,
    ): SecurityDialog =
        SecurityDialog().apply {
            this.security = type
            this.arguments = bundleOf(ARG_SECURITY_TYPE to type)
        }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ARG_SECURITY_TYPE, security)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (savedInstanceState != null) {
            this.security = arguments?.getParcelable(ARG_SECURITY_TYPE)
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

                // Section password create
                SecurityStateContract.SuccessOnCreatePassword -> {
                    requireParentFragment().setFragmentResult(
                        SUCCESS_ADD_PASSWORD_RESULT,
                        bundleOf(
                            BUNDLE_PASSWORD_RESULT_SECURITY_TYPE to security,
                            BUNDLE_IS_PASSWORD_CREATION to true
                        )
                    )
                    dismiss()
                }

                // Section password update
                SecurityStateContract.SuccessOnUpdatePassword -> {
                    requireParentFragment().setFragmentResult(
                        SUCCESS_ADD_PASSWORD_RESULT,
                        bundleOf(
                            BUNDLE_PASSWORD_RESULT_SECURITY_TYPE to security,
                            BUNDLE_IS_PASSWORD_CREATION to false
                        )
                    )
                    dismiss()
                }

                // Only for Task password update
                is SecurityStateContract.UpdateTaskPassword -> {
                    Log.e("SECURITY_DIALOG", "UpdateTaskPassword")
                    requireParentFragment().setFragmentResult(
                        SUCCESS_ADD_PASSWORD_RESULT,
                        bundleOf(
                            BUNDLE_PASSWORD_RESULT_SECURITY_TYPE to security,
                            BUNDLE_IS_PASSWORD_CREATION to false,
                            BUNDLE_PASSWORD_VALUE to state.passwordFromUser
                        )
                    )
                    dismiss()
                }

                // Only for Task create password
                is SecurityStateContract.CreateTaskPassword -> {
                    Log.e("SECURITY_DIALOG", "CreateTaskPassword")
                    requireParentFragment().setFragmentResult(
                        SUCCESS_ADD_PASSWORD_RESULT,
                        bundleOf(
                            BUNDLE_PASSWORD_RESULT_SECURITY_TYPE to security,
                            BUNDLE_IS_PASSWORD_CREATION to true,
                            BUNDLE_PASSWORD_VALUE to state.passwordFromUser
                        )
                    )
                    dismiss()
                }

                /** Check password */
                SecurityStateContract.ErrorOnCheckPassword -> {
                    binding.securityDialogLabel.text = getString(R.string.wrong_password)
                    binding.securityDialogPassword.text = EMPTY_STRING
                }

                is SecurityStateContract.SuccessOnCheckPassword -> {
                    requireParentFragment().setFragmentResult(
                        SUCCESS_CHECK_PASSWORD_RESULT,
                        bundleOf(
                            BUNDLE_PASSWORD_RESULT_SECURITY_TYPE to security,
                        )
                    )
                    dismiss()
                }

                /** Can't found security item for check password */
                is SecurityStateContract.ErrorNotFoundSecurityItemToCheckPassword -> dismiss()
                /** Can't found section id */
                is SecurityStateContract.ErrorSectionIdIsNull -> dismiss()
                /** Can't found task id */
                is SecurityStateContract.ErrorTaskIdIsNull -> dismiss()
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
                security?.let { securityType ->
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

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.92

        const val ARG_SECURITY_TYPE = "ARG_SECURITY_TYPE"
    }
}
