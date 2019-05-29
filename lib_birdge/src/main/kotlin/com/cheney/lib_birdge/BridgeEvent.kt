package com.cheney.lib_birdge

import org.json.JSONObject

class BridgeEvent {

    interface EventType {
        companion object {
            val REQUEST: String
                get() = "request"
            val RESPONSE: String
                get() = "response"
        }
    }

    lateinit var type: String
    lateinit var module: String
    lateinit var method: String
    lateinit var method_serial: Any
    lateinit var params: HashMap<String,Any?>


    fun toEventString(): String {
        val obj = JSONObject()
        when (type) {
            EventType.REQUEST -> obj.put("type", "request")
            EventType.RESPONSE -> obj.put("type", "response")
        }
        obj.put("module", module)
        obj.put("method", method)
        obj.put("method_serial", method_serial)
        obj.put("params", params)
        return obj.toString()
    }


    fun toResponse(): BridgeEvent {
        val response = BridgeEvent()
        response.type = EventType.RESPONSE
        response.module = this.module
        response.method = this.method
        response.method_serial = this.method_serial
        return response
    }


    fun success(result: String): BridgeEvent? {
        if (this.type != EventType.RESPONSE) {
            return null
        }
        this.params = hashMapOf()
        this.params.put("result", result)
        return this
    }

    fun successDefault(): BridgeEvent? {
        if (this.type != EventType.RESPONSE) {
            return null
        }
        this.params = hashMapOf()
        this.params.put("result", "success")
        return this
    }

    fun error(code: String, message: String): BridgeEvent? {
        if (this.type != EventType.RESPONSE) {
            return null
        }
        val err = JSONObject()
        with(err) {
            put("code", code)
            put("message", message)
        }
        this.params = hashMapOf()
        this.params.put("error", err)
        return this
    }

    fun send(bridge: XJsBridge) {
        bridge.nativeToJs(toEventString())
    }
}