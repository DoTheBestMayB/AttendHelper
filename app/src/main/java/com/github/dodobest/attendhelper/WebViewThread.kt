package com.github.dodobest.attendhelper

import android.os.Handler
import android.os.Looper

class WebViewThread(
    var url: String,
    @Volatile
    var processHandler: ProcessHandler = ProcessHandler(),
    var handler: Handler? = Handler()
) : Thread() {
    private lateinit var looper: Looper

    init {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "https://$url"
    }

    override fun run() {
        Looper.prepare()
        

    }
}