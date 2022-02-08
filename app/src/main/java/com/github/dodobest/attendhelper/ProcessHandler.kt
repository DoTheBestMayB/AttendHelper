package com.github.dodobest.attendhelper

import android.os.Handler
import android.os.Message
import android.util.Log

class ProcessHandler : Handler() {
    override fun handleMessage(msg: Message) {
        val output = msg.obj as String
        if (msg.what == WebViewActivityTm.URL && output.contains("Done")) {
            Log.d(WebViewActivityTm.TAG, "LoadURL Done")
            if (looper != null) looper.quit()
        } else if (msg.what == WebViewActivityTm.JS && !output.contains("null")) {
            Log.d(WebViewActivityTm.TAG, "JS Done")
            if (looper != null) looper.quit()
        }
    }
}