package com.github.dodobest.attendhelper

import android.os.Handler
import android.os.Message
import android.util.Log

class ProcessHandler : Handler() {
    override fun handleMessage(msg: Message) {
        val output = msg.obj as String
        if (msg.what == WebViewActivityT.URL && output.contains("Done")) {
            Log.d(WebViewActivityT.TAG, "LoadURL Done")
            if (looper != null) looper.quit()
        } else if (msg.what == WebViewActivityT.JS && !output.contains("null")) {
            Log.d(WebViewActivityT.TAG, "JS Done")
            if (looper != null) looper.quit()
        }
    }
}