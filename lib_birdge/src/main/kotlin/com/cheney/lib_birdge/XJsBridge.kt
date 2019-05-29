package com.cheney.lib_birdge

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.alibaba.fastjson.JSON
import java.lang.ref.WeakReference
import java.lang.reflect.Method


class XJsBridge(webView: WebView) {

    private var mWebView: WeakReference<WebView> = WeakReference(webView)

    private var mModuleFunMap = HashMap<String, MutableList<Method>>()

    private var mModuleMap = HashMap<String, XBridgeBaseModule>()

    private val mHandler = Handler(Looper.getMainLooper())

    companion object {
        val BridgeName
            get() = "XJsBridge"

        val BridgeVersion
            get() = "1.0.0"

        val Bridge_JSNameSpace
            get() = "kagura"

        val TAG: String = "XJsBridge"
    }

    val injectJs
        get() = """
       (
         function(){
            if (window.internal_bridge) {
                return;
            }
            const MethodsCallStack = function() {
                var entry = new Object();

                function randomSN() {
                    var time = new Date().getTime();
                    var round = Math.round(Math.random() * 100000);
                    var sn = time + round;
                    return sn + "";
                }

                this.addMethod = function(callback) {
                    var sn;
                    sn = randomSN();
                    if (callback) {
                        var time = new Date().getTime();
                        entry[sn] = {
                            "time": time,
                            "callback": callback
                        };
                    }
                    return sn;
                }

                this.fetchCallback = function(sn) {
                    var value = entry[sn];
                    if (value) {
                        delete entry[sn];
                        return value.callback;
                    }
                }

                this.loopForTimeout = function() {
                    setInterval(function() {
                        Object.keys(entry).forEach(function(key) {
                            var value = entry[key];
                            var current = new Date().getTime();
                            if (current - value.time > 600 * 1000) {
                                if (value && value.callback) {
                                    delete entry[key];
                                } else {
                                    delete entry[key];
                                }
                            }
                        });
                    }, 1000);
                }
            }

            const bridge = {};
            bridge.name = "${BridgeName}";
            bridge.version = "${BridgeVersion}";
            bridge.send = function(message) {
                window.${BridgeName}.jsToNative(message);
            };

            bridge.methodsCallStack = new MethodsCallStack();

            bridge.nativeToJS = function(event) {
                if (event["type"] == "response") {
                    var callback = this.methodsCallStack.fetchCallback(event["method_serial"]);
                    if (callback) {
                        if (event["params"]) {
                            console.log('callback pk.-----',event["params"]["result"])
                            callback(event["params"]["error"], event["params"]["result"]);
                        }
                    }
                } else if (event["type"] == "request") {

                }
            }

            bridge.jsToNative = function(type, module, method, params, callback) {
                var method_serial = this.methodsCallStack.addMethod(callback);
                var event = {
                    "type": type,
                    "module": module,
                    "method": method,
                    "method_serial": method_serial,
                    "params": params
                };
                var event_str = JSON.stringify(event);
                this.send(event_str);
            }

            window.internal_bridge = bridge;
            console.log('The taro is ready.');
        })(window._debug && console && console.log);

        function bridgeReq(moduleName, methodName, params, callback) {
            window.internal_bridge.jsToNative("request", moduleName, methodName, params, callback);
        }

        var ${Bridge_JSNameSpace} = window.${Bridge_JSNameSpace} || {};
        """


    fun registerModule(module: XBridgeBaseModule) {
        findMethod(module)
    }


    private fun findMethod(module: XBridgeBaseModule) {
        val methods = module.javaClass.methods
        if (methods.isEmpty()) {
            return
        }
        val bridgeMethodList = mutableListOf<Method>()
        for (method in module.javaClass.methods) {
            if (!method.isAnnotationPresent(XBridgeMethod::class.java)) {
                continue
            }
            bridgeMethodList.add(method)
        }
        mModuleFunMap[module.moduleName()] = bridgeMethodList
        mModuleMap[module.moduleName()] = module
    }

    fun nativeToJs(message: String) {
        Log.d(TAG, "nativeToJs is called $message")
        val script = "window.internal_bridge.nativeToJS($message);"
        mHandler.post {
            mWebView.get()?.evaluateJavascript(script, null)
        }
    }


    @JavascriptInterface
    fun jsToNative(message: String) {
        Log.d(TAG, "jsToNative is called $message")
        val bridgeEvent: BridgeEvent? = JSON.parseObject(message, BridgeEvent::class.java)
                ?: return
        val moduleName = bridgeEvent!!.module
        if (TextUtils.isEmpty(moduleName) || null == mModuleMap[moduleName]) {
            bridgeEvent.toResponse()
                    .error("-1", "moduleName params miss")
                    ?.send(this)
            return
        }

        val methodList = mModuleFunMap[moduleName]
        if (null == methodList) {
            bridgeEvent.toResponse()
                    .error("-1", "methodList empty")
                    ?.send(this)
            return
        }
        val methodFilterList = methodList.filter {
            it.name == bridgeEvent.method
        }
        if (methodFilterList.isEmpty()) {
            bridgeEvent.toResponse().error("-1", "method not found")
        }
        methodFilterList[0].invoke(mModuleMap[moduleName], bridgeEvent)
    }


}