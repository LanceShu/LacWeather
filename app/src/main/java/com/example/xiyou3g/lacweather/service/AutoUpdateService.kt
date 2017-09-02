package com.example.xiyou3g.lacweather.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.preference.PreferenceManager
import com.example.xiyou3g.lacweather.util.HttpUtil
import com.example.xiyou3g.lacweather.util.LogUtil
import com.example.xiyou3g.lacweather.util.Utility
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by Lance on 2017/8/19.
 */

class AutoUpdateService: Service(){
    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateWeather()
        updateBingPic()
        val manager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val anHour = 8*60*60*1000
        val triggerAttime = SystemClock.elapsedRealtime()+anHour
        val i = Intent(this,AutoUpdateService::class.java)
        val pi = PendingIntent.getService(this,0,i,0)
        manager.cancel(pi)
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAttime,pi)
        return super.onStartCommand(intent, flags, startId)
    }

    fun updateWeather(){
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val weatherString = prefs.getString("weather",null)
        if(weatherString != null){
            val weather = Utility.handleWeatherResponse(weatherString)
            val weatherId = weather!!.basic!!.weatherId
            val weatherUrl = "http://guolin.tech/api/weather?cityid=$weatherId&key=710e976d83e54ad9b139b42267ba4ce7"
            LogUtil.e("weatherUrl",weatherUrl)
            HttpUtil.sendOkHttpRequest(weatherUrl,object : Callback{
                override fun onFailure(call: Call?, e: IOException?) {

                    e!!.printStackTrace()
                }

                override fun onResponse(call: Call?, response: Response?) {

                    val responseText = response!!.body().string()
                    LogUtil.e("autoserviceresponse",responseText)
                    val weather = Utility.handleWeatherResponse(responseText)
                    if(weather != null && "ok".equals(weather.status)){
                        val editor = PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateService).edit()
                        editor.putString("weather",responseText)
                        editor.apply()
                    }
                }

            })
        }
    }

    fun updateBingPic(){
        val requestBingPic = "http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requestBingPic,object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {

                e!!.printStackTrace()
            }

            override fun onResponse(call: Call?, response: Response?) {

                val bingPic = response!!.body().string()
                val editor = PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateService).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()

            }

        })
    }

}