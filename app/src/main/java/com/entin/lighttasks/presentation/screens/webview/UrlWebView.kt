package com.entin.lighttasks.presentation.screens.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.entin.lighttasks.databinding.WebViewBinding

class UrlWebView : Fragment() {

    private var _binding: WebViewBinding? = null
    private val binding get() = _binding!!
    private val args: UrlWebViewArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openWebView(args.url)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = WebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(url: String) {
        binding.progressBar.max = 100

        binding.webview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(url)
        }

        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webview.apply {
            removeAllViews()
            clearCache(true)
            destroy()
        }
    }
}
