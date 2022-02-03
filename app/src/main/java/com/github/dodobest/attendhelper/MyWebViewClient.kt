package com.github.dodobest.attendhelper

import android.os.Message
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d(TAG, "WebView get URL which is $url")

        val message: Message = Message.obtain()
        message.obj = "Done"
        message.what = URL
    }

    companion object {
        const val TAG = "WebViewClient"
        const val URL = 1
    }
}