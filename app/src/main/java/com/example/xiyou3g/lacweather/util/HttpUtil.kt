package com.example.xiyou3g.lacweather.util

import okhttp3.OkHttpClient
import okhttp3.Request
import javax.security.auth.callback.Callback

/**
 * Created by Lance on 2017/8/15.
 */

object HttpUtil{

    fun sendOkHttpRequest(address : String,callback: okhttp3.Callback){
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(address)
                .build()
        client.newCall(request).enqueue(callback)
    }

}