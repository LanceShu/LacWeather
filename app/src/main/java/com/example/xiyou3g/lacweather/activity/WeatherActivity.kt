package com.example.xiyou3g.lacweather.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.example.xiyou3g.lacweather.R
import com.example.xiyou3g.lacweather.gson.Weather
import com.example.xiyou3g.lacweather.service.AutoUpdateService
import com.example.xiyou3g.lacweather.util.HttpUtil
import com.example.xiyou3g.lacweather.util.LogUtil
import com.example.xiyou3g.lacweather.util.Utility
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.aqi.*
import kotlinx.android.synthetic.main.forecast_item.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.suggestion.*
import kotlinx.android.synthetic.main.title.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * Created by Lance on 2017/8/17.
 */

class WeatherActivity: AppCompatActivity(){

    private var weatherLayout: ScrollView? = null
    private var forecastLayout: LinearLayout?  = null
    private var navView: NavigationView? = null

    companion object{
        var drawerLayout: DrawerLayout? = null
        var swipeRefresh: SwipeRefreshLayout? = null
        fun requestWeather(weatherId: String?){
            this.requestWeather(weatherId)
        }
    }

    private var mWeatherId: String? = null
    private var weather: Weather? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT >= 21){
            val decerView = window.decorView
            decerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_weather)
        initWight()

        val nav_change = intent.getIntExtra("nav_change",-1)
        LogUtil.e("nav_change",nav_change.toString())
        if(nav_change == 1){
            drawerLayout!!.closeDrawer(GravityCompat.END)
            weatherLayout!!.visibility = View.INVISIBLE
            swipeRefresh!!.isRefreshing = true
            mWeatherId = intent.getStringExtra("weather_id")
            LogUtil.e("change refresh",mWeatherId.toString())
            requestWeather(mWeatherId)
        }else{
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val weatherString: String? = prefs.getString("weather",null)
            if(weatherString != null){
                /*有缓存时直接解析天气数据*/
                weather = Utility.handleWeatherResponse(weatherString)
                mWeatherId = weather!!.basic!!.weatherId
                showWeatehrInfo(weather!!)
            }else{
                /*无缓存时去服务器查询天气*/
                mWeatherId = intent.getStringExtra("weather_id")
                weatherLayout!!.visibility = View.INVISIBLE
                requestWeather(mWeatherId)
            }

            val bingPic = prefs.getString("bing_pic",null)
            if(bingPic != null){
                Glide.with(this).load(bingPic).into(bing_pc_img)
                Glide.with(this).load(bingPic).into(navView!!.getHeaderView(0).header_image)
            }else{
                loadBingPic()
            }
        }

        swipeRefresh!!.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {

                LogUtil.e("weatherId",mWeatherId.toString())
                requestWeather(mWeatherId)
            }

        })
    }

    /*加载必应图片*/
    private fun loadBingPic() {

        val requestBingPic = "http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requestBingPic,object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {

                e!!.printStackTrace()
            }

            override fun onResponse(call: Call?, response: Response?) {

                val bingPic = response!!.body().string()
                val editor = PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()
                runOnUiThread({
                    Glide.with(this@WeatherActivity).load(bingPic).into(bing_pc_img)
                    Glide.with(this@WeatherActivity).load(bingPic).into(navView!!.getHeaderView(0).header_image)
                })
            }

        })
    }

    /*根据天气id请求城市天气信息*/
    fun requestWeather(weatherId: String?) {

        val weatherUrl = "http://guolin.tech/api/weather?cityid=$weatherId&key=710e976d83e54ad9b139b42267ba4ce7"

        HttpUtil.sendOkHttpRequest(weatherUrl,object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {

                runOnUiThread({
                    Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
                    swipeRefresh!!.isRefreshing = false
                })
            }

            override fun onResponse(call: Call?, response: Response?) {

                val responseText = response!!.body().string()
                LogUtil.e("responseText",responseText)
                weather = Utility.handleWeatherResponse(responseText)
                runOnUiThread( {
                    if(weather != null && "ok".equals(weather!!.status)){
                        val editor = PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                        editor.putString("weather",responseText)
                        editor.apply()
                        showWeatehrInfo(weather!!)
                    }else{
                        Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
                    }
                    swipeRefresh!!.isRefreshing = false
                })
            }
        })
        loadBingPic()
    }

    /*处理并展示Weather实体类中的数据*/
    private fun showWeatehrInfo(weather: Weather) {

        if(weather != null && weather.status.equals("ok")){
            val cityName = weather.basic!!.cityName
            val updateTime = weather.basic!!.update!!.updateTime!!.split(" ")[1]
            val degree = weather.now!!.temperature + "℃"
            val weatherInfo = weather.now!!.more!!.info
            title_city.text = cityName
            title_update_time.text = updateTime
            degree_text.text = degree
            weather_info_text.text = weatherInfo

            navView!!.getHeaderView(0).header_city.text = cityName
            navView!!.getHeaderView(0).header_tmp.text = degree
            navView!!.getHeaderView(0).header_info.text = weatherInfo

            forecastLayout!!.removeAllViews()
            for(forecast in weather.forecastList!!){
                val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
                val dateText = view.findViewById(R.id.date_text) as TextView
                val infoText = view.findViewById(R.id.info_text) as TextView
                val maxText = view.findViewById(R.id.max_text) as TextView
                val minText = view.findViewById(R.id.min_text) as TextView
                dateText.text = forecast.date
                infoText.text = forecast.more!!.inf
                maxText.text = forecast.temperature!!.max + "℃"
                minText.text = forecast.temperature!!.min + "℃"
                forecastLayout!!.addView(view)
            }
            if(weather.aqi != null){
                aqi_text.text = weather!!.aqi!!.city!!.aqi
                pm25_text.text = weather!!.aqi!!.city!!.pm25
            }
            val comfort = "舒适度：\n"+weather.suggestion!!.comfort!!.info
            val carWash = "洗车指数：\n"+weather.suggestion!!.carWash!!.info
            val sport = "运动指数：\n"+weather.suggestion!!.sport!!.info
            comfort_text.text = comfort
            car_wash_text.text = carWash
            sport_text.text = sport
            weatherLayout!!.visibility = View.VISIBLE

            val intent = Intent(this@WeatherActivity,AutoUpdateService::class.java)
            startService(intent)
        }else{
            Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
        }

    }

    private fun initWight(){
        weatherLayout = findViewById(R.id.weather_layout) as ScrollView
        forecastLayout = findViewById(R.id.forecast_layout) as LinearLayout
        swipeRefresh = findViewById(R.id.swipe_refresh) as SwipeRefreshLayout

        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        navView = findViewById(R.id.nav_view) as NavigationView
        navView!!.setCheckedItem(R.id.nav_my_city)

        swipeRefresh!!.setColorSchemeColors(R.color.colorAccent,R.color.colorPrimary)

        nav_button.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                drawerLayout!!.openDrawer(GravityCompat.START)
            }
        })

        navView!!.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.nav_my_city->{
                        navView!!.setCheckedItem(R.id.nav_my_city)
                        drawerLayout!!.closeDrawers()
                        showWeatehrInfo(weather!!)
                    }
                    R.id.nav_change_city->{
//                        navView!!.setCheckedItem(R.id.nav_change_city)
                        drawerLayout!!.closeDrawer(GravityCompat.START)
                        drawerLayout!!.openDrawer(GravityCompat.END)
                    }
                    R.id.nav_about->{
                        navView!!.setCheckedItem(R.id.nav_about)
                        drawerLayout!!.closeDrawers()
                    }
                    R.id.nav_help->{
                        navView!!.setCheckedItem(R.id.nav_help)
                        drawerLayout!!.closeDrawers()
                    }
                }
                return true
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitClick()
        }
        return false
    }

    private var isExit: Boolean? = false

    private fun exitClick() {
        val timer: Timer
        if (isExit === false) {
            isExit = true
            Toast.makeText(this@WeatherActivity,"再按一次退出程序",Toast.LENGTH_SHORT).show()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false
                }
            }, 2000)
        } else {
            finish()
        }
    }

}