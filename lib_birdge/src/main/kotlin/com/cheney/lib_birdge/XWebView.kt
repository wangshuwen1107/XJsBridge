package com.cheney.lib_birdge

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView
import java.lang.ref.WeakReference


abstract class XWebView : WebView {

    private lateinit var xBridge: XJsBridge

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        xBridge = XJsBridge(this)
        createModules()?.let { modules ->
            modules.forEach {
                it.bridge = xBridge
                it.context = WeakReference(context)
                xBridge.registerModule(it)
            }
        }
        addJavascriptInterface(xBridge, XJsBridge.BridgeName)
        webChromeClient = XWebChromeClient()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    abstract fun createModules(): List<XBridgeBaseModule>?

    fun injectJS() {
        post {
            evaluateJavascript(xBridge.injectJs, null)
        }
    }

}