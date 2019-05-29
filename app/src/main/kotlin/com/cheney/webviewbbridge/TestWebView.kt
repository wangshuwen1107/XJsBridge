package com.cheney.webviewbbridge

import android.content.Context
import android.util.AttributeSet
import com.cheney.lib_birdge.XBridgeBaseModule
import com.cheney.lib_birdge.XWebView

class TestWebView : XWebView {

    override fun createModules(): List<XBridgeBaseModule>? = listOf<XBridgeBaseModule>(MyModule())

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

}