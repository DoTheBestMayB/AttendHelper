package com.github.dodobest.attendhelper

import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity(), WebContract.View {
    lateinit var
    companion object {
        private const val TAG = "WebViewActivity"
        private const val URL = 1
        private const val JS = 2
        private const val ID = "id"
        private const val PW = "1234"
    }
}