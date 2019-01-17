package com.example.xiyou3g.lacweather.util

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Created by Lance
 * on 2017/8/15.
 */

object HttpUtil{
    fun sendOkHttpRequest(address : String, callback: okhttp3.Callback){
        val client = OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build()
        val request = Request.Builder()
                .url(address)
                .build()
        client.newCall(request).enqueue(callback)
    }
}