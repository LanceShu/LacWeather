package com.example.xiyou3g.lacweather.util

import android.app.Application
import android.content.Context

import org.litepal.LitePal

/**
 * Created by Lance on 2017/8/15.
 */

class MyApplication : Application() {

    override fun onCreate() {
        context = applicationContext

        /*初始化LitePal数据库*/
        LitePal.initialize(context)
    }

    companion object {

        var context: Context? = null
            private set
    }
}
