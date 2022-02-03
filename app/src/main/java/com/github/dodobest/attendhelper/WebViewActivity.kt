package com.github.dodobest.attendhelper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.dodobest.attendhelper.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity(), WebViewContract.View {
    private lateinit var binding: ActivityWebviewBinding
    private lateinit var presenter: WebViewContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = WebViewPresenter()

        setWebViewSetting()
    }

    private fun setWebViewSetting() {
        binding.webView.settings.javaScriptEnabled
        binding.webView.webChromeClient =
    }
}