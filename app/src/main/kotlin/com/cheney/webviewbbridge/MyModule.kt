package com.cheney.webviewbbridge

import android.app.ProgressDialog
import android.util.Log
import android.widget.Toast
import com.cheney.lib_birdge.BridgeEvent
import com.cheney.lib_birdge.XBridgeBaseModule
import com.cheney.lib_birdge.XBridgeMethod

class MyModule : XBridgeBaseModule() {

    override fun moduleName(): String = "test"

    private val loadingDialog: ProgressDialog? by lazy {
        context?.get()?.let {
            ProgressDialog(it)
        }

    }

    @XBridgeMethod
    fun test(event: BridgeEvent) {
        val username = event.params["username"]
        val pwd = event.params["pwd"]
        Log.d("MyModule", "test is called ")
        context?.get()?.let {
            Toast.makeText(it, "用户名$username 密码$pwd", Toast.LENGTH_LONG).show()
        }
        event.toResponse().successDefault()?.send(bridge)
    }


    @XBridgeMethod
    fun showLoading(event: BridgeEvent) {
        Log.d("MyModule", "showLoading is called ")
        context?.get()?.let {
            loadingDialog?.let {
                it.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                it.show()
            }
        }
        event.toResponse().successDefault()?.send(bridge)
    }


}