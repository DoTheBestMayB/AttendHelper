package com.github.dodobest.attendhelper

interface WebViewContract {
    interface View {

    }

    interface Presenter {

        fun startWebView(url: String)
    }
}