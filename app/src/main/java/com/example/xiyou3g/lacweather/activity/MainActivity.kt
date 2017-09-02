package com.example.xiyou3g.lacweather.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.example.xiyou3g.lacweather.R
import com.example.xiyou3g.lacweather.gson.Weather
import com.example.xiyou3g.lacweather.util.MyApplication
import java.util.*

class MainActivity : AppCompatActivity() {

    private val forecastLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getString("weather",null) != null){
            val intent = Intent(this,WeatherActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
       if(keyCode == KeyEvent.KEYCODE_BACK){
           exitClick()
       }
        return false
    }

    private var isExit: Boolean? = false

    private fun exitClick(){
        val timer: Timer
        if(isExit == false){
            isExit = true
            Toast.makeText(this@MainActivity,"再按一次退出",Toast.LENGTH_SHORT).show()
            timer = Timer()
            timer.schedule(object : TimerTask(){
                override fun run() {
                    isExit = false
                }

            },2000)
        }else{
            finish()
        }
    }


}