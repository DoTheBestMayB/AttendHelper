package com.github.dodobest.attendhelper

class WebViewPresenter() : WebViewContract.Presenter {
    private lateinit var thread: WebViewThread

    override fun startWebView(url: String) {
        thread = WebViewThread(url)
        thread.start()
    }

    companion object {
        private const val TAG = "WebViewActivity"
        private const val URL = 1
        private const val JS = 2
        private const val ID = "id"
        private const val PW = "1234"
    }
}