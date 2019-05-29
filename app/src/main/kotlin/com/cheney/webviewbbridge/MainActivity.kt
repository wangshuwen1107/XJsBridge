package com.cheney.webviewbbridge

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cheney.lib_birdge.XWebView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<XWebView>(R.id.main_wv).loadUrl("file:///android_asset/demo.html")
    }

}
