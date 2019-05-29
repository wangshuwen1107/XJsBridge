package com.cheney.lib_birdge

import android.content.Context
import java.lang.ref.WeakReference

abstract class XBridgeBaseModule {

    lateinit var bridge: XJsBridge

    var context: WeakReference<Context>? = null

    abstract fun moduleName(): String


}