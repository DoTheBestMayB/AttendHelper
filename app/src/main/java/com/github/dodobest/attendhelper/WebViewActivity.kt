package com.github.dodobest.attendhelper

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.github.dodobest.attendhelper.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity(), WebViewContract.View {
    private lateinit var binding: ActivityWebviewBinding
    private lateinit var presenter: WebViewContract.Presenter
    private lateinit var webView: WebView
    private lateinit var processHandler: ProcessHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = WebViewPresenter()
        webView = binding.webView
        processHandler = ProcessHandler()
        setWebViewSetting()
        setListener()
    }

    private fun setListener() {
        binding.button.setOnClickListener {
            if (currentFocus != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
            Log.d(TAG, "onClick Start")
            presenter.startWebView(binding.urlText.text.toString())
        }
    }

    private fun setWebViewSetting() {
        webView.settings.javaScriptEnabled
        webView.webChromeClient = ChromeClient()
        webView.webViewClient = MyWebViewClient()
    }

    private fun loadUrlWithHandler(url: String) {
        processHandler.post { webView.loadUrl(url) }
    }

    companion object {
        private const val TAG = "WebviewActivity"
    }
}