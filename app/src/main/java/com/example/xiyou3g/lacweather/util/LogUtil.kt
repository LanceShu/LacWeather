package com.example.xiyou3g.lacweather.util

import android.util.Log

/**
 * Created by Lance on 2017/8/15.
 */

object LogUtil {
    val VERBOSE = 1
    val DEBUG = 2
    val INFO = 3
    val WARN = 4
    val ERROR = 5
    val NOTHING = 6
    var level = com.example.xiyou3g.lacweather.util.LogUtil.VERBOSE

    fun v(tag: String, msg: String) {
        if (com.example.xiyou3g.lacweather.util.LogUtil.level <= com.example.xiyou3g.lacweather.util.LogUtil.VERBOSE) {
            android.util.Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (com.example.xiyou3g.lacweather.util.LogUtil.level <= com.example.xiyou3g.lacweather.util.LogUtil.DEBUG) {
            android.util.Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (com.example.xiyou3g.lacweather.util.LogUtil.level <= com.example.xiyou3g.lacweather.util.LogUtil.INFO) {
            android.util.Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (com.example.xiyou3g.lacweather.util.LogUtil.level <= com.example.xiyou3g.lacweather.util.LogUtil.WARN) {
            android.util.Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (com.example.xiyou3g.lacweather.util.LogUtil.level <= com.example.xiyou3g.lacweather.util.LogUtil.ERROR) {
            android.util.Log.e(tag, msg)
        }
    }
}
