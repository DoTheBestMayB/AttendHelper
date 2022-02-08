package com.github.dodobest.attendhelper

import android.os.Message
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient

class ChromeClient : WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        if (consoleMessage == null) return super.onConsoleMessage(consoleMessage)

        val msg = consoleMessage.message()
        if (msg.contains(PASS_MESSAGE)) return super.onConsoleMessage(consoleMessage)

        Log.d(TAG, "console Message: $msg")

        val message = Message()
        message.obj = msg
        message.what = JS
        handleMessage(message)

        return super.onConsoleMessage(consoleMessage)
    }

    private fun handleMessage(message: Message) {
        TODO()
    }

    companion object {
        const val PASS_MESSAGE = "Mixed Content"
        const val TAG = "ChromeClient"
        const val JS = 2
    }
}