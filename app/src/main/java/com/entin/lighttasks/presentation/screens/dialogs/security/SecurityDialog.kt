package com.entin.lighttasks.presentation.screens.dialogs.security

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SecurityDialogBinding
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SecurityDialog : DialogFragment() {

    private var _binding: SecurityDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SecurityDialogViewModel by viewModels()

    private var type: SecurityType? = null
    private var onSuccess: ((String) -> Unit)? = null

    fun newInstance(type: SecurityType, onSuccess: (String) -> Unit): SecurityDialog =
        SecurityDialog().apply {
            this.type = type
            this.onSuccess = onSuccess
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = true
        _binding = SecurityDialogBinding.inflate(inflater, container, false)

        with(binding) {

            stateObserver()

            securityDialogClear.setOnClickListener { clearInputCode() }

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
                type?.let {
                    if(insertedPassword.isNotEmpty() && insertedPassword.isNotBlank()) {
                        if(insertedPassword.length >= 4) {
                            viewModel.onSubmitClicked(it, insertedPassword)
                        } else {
                            binding.securityDialogLabel.text =
                                getString(R.string.minimum_4_characters)
                        }
                    } else {
                        binding.securityDialogLabel.text = getString(R.string.minimum_4_characters)
                    }
                }
            }
        }

        return binding.root
    }

    private fun stateObserver() {
        viewModel.action.observe(viewLifecycleOwner) { state: SecurityStateContract ->
            when(state) {
                SecurityStateContract.ErrorOnRepeatPassword -> {
                    binding.securityDialogLabel.text =
                        getString(R.string.passwords_are_not_matching)
                    binding.securityDialogPassword.text = EMPTY_STRING
                }
                SecurityStateContract.RepeatPassword -> {
                    binding.securityDialogLabel.text = getString(R.string.repeat_password)
                    binding.securityDialogPassword.text = EMPTY_STRING
                }
                SecurityStateContract.SuccessOnRepeatPassword -> {
                    onSuccess?.let {
                        it(binding.securityDialogPassword.text.toString())
                    }
                    dismiss()
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
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.65
    }
}
