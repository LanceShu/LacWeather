package com.example.xiyou3g.lacweather.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.xiyou3g.lacweather.R
import com.example.xiyou3g.lacweather.asynctask.GetLocalCityAsyncTask
import com.example.xiyou3g.lacweather.gson.Weather
import com.example.xiyou3g.lacweather.receiver.UpdataBroadcastRecevier
import com.example.xiyou3g.lacweather.service.AutoUpdateService
import com.example.xiyou3g.lacweather.util.*
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.aqi.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.suggestion.*
import kotlinx.android.synthetic.main.title_bar.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by
 * Lance on 2017/8/17.
 */

class WeatherActivity: AppCompatActivity(), View.OnClickListener{
    private var weatherLayout: ScrollView? = null
    private var forecastLayout: LinearLayout?  = null
    private var navView: NavigationView? = null
    private var mWeatherId: String? = null
    private var weather: Weather? = null
    private var drawerLayout: DrawerLayout? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private val TAG = "WeatherActivity"
    private val receiver = UpdataBroadcastRecevier()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decerView = window.decorView
        decerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        // 注册广播；
        registerBroadRecevier()
        // 初始化控件；
        initWight()
        // 获取从哪启动的activity;
        val nav_change = intent.getIntExtra("nav_change",-1)
        LogUtil.e("nav_change",nav_change.toString())
        if(nav_change == 1){
            weatherLayout!!.visibility = View.INVISIBLE
            swipeRefresh!!.isRefreshing = true
            mWeatherId = intent.getStringExtra("weather_id")
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
                weatherLayout!!.visibility = View.INVISIBLE
                swipeRefresh!!.isRefreshing = true
                mWeatherId = intent.getStringExtra("weather_id")
                requestWeather(mWeatherId)
            }
            // 加载必应背景图；
//            val bingPic = prefs.getString("bing_pic",null)
//            if(bingPic != null){
//                Glide.with(this).load(bingPic).into(bing_pc_img)
//                Glide.with(this).load(bingPic).into(navView!!.getHeaderView(0).header_image)
//            }else{
//                loadBingPic()
//            }
        }
        swipeRefresh!!.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                LogUtil.e("weatherId",mWeatherId.toString())
                requestWeather(mWeatherId)
            }
        })
    }

    private fun registerBroadRecevier() {
        val filter = IntentFilter()
        filter.addAction("android.intent.action.DOWNLOAD_COMPLETE")
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onStart() {
        super.onStart()
        if (isVisiableToNavAddBtn(mWeatherId)) {
            nav_add_care.visibility = View.VISIBLE
        } else {
            nav_add_care.visibility = View.GONE
        }
    }

    /*加载必应图片*/
    private fun loadBingPic() {
        val requestBingPic = "http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requestBingPic, object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                e!!.printStackTrace()
            }

            override fun onResponse(call: Call?, response: Response?) {
                val bingPic = response!!.body()?.string()
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
        mWeatherId = weatherId
        val weatherUrl = "http://guolin.tech/api/weather?cityid=$weatherId&key=710e976d83e54ad9b139b42267ba4ce7"
        HttpUtil.sendOkHttpRequest(weatherUrl, object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread({
                    Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
                    swipeRefresh!!.isRefreshing = false
                })
            }

            override fun onResponse(call: Call?, response: Response) {
                val responseText = response.body()?.string()
                LogUtil.e("responseText", responseText!!)
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
//        loadBingPic()
    }

    /*处理并展示Weather实体类中的数据*/
    @SuppressLint("SetTextI18n")
    private fun showWeatehrInfo(weather: Weather) {
        if(weather != null && weather.status.equals("ok")){
            if (isVisiableToNavAddBtn(mWeatherId)) {
                nav_add_care.visibility = View.VISIBLE
            } else {
                nav_add_care.visibility = View.GONE
            }
            val cityName = weather.basic!!.cityName
            val updateTime = weather.basic!!.update!!.updateTime!!.split(" ")[1]
            val degree = weather.now!!.temperature + "℃"
            val weatherInfo = weather.now!!.more!!.info
            val localIcon = findViewById(R.id.nav_local) as ImageView
            GetLocalCityAsyncTask(localIcon, cityName).execute()
            nav_weather_icon.setImageResource(IconUtils.getWeatherWhiteIconByWeather(weatherInfo))
            title_city.text = cityName
            nav_add_care.setOnClickListener(this@WeatherActivity)
            nav_share.setOnClickListener(this@WeatherActivity)
//            title_update_time.text = "更新于 " + updateTime
            now_update_time.text = "更新于 " + updateTime
            degree_text.text = degree
            weather_info_text.text = weatherInfo

            navView!!.getHeaderView(0).header_city.text = cityName
            navView!!.getHeaderView(0).header_tmp.text = degree
            navView!!.getHeaderView(0).header_info.text = weatherInfo

            forecastLayout!!.removeAllViews()
            var i = 0
            for(forecast in weather.forecastList!!){
                i ++
                val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
                val dateText = view.findViewById(R.id.date_text) as TextView
                val timeText = view.findViewById(R.id.time_text) as TextView
                val infoText = view.findViewById(R.id.info_text) as TextView
                val maxText = view.findViewById(R.id.max_text) as TextView
                val minText = view.findViewById(R.id.min_text) as TextView
                dateText.text = forecast.date
                infoText.text = forecast.more!!.inf
                maxText.text = forecast.temperature!!.max + "℃"
                minText.text = forecast.temperature!!.min + "℃"
                when(i) {
                    1 -> timeText.text = "今天"
                    2 -> timeText.text = "明天"
                    3 -> timeText.text = "后天"
                }
                forecastLayout!!.addView(view)
            }
            if(weather.aqi != null){
                aqi_text.text = weather.aqi!!.city!!.aqi
                pm25_text.text = weather.aqi!!.city!!.pm25
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

    @SuppressLint("ResourceAsColor")
    private fun initWight(){
        nav_share.visibility = View.VISIBLE
        weatherLayout = findViewById(R.id.weather_layout) as ScrollView
        forecastLayout = findViewById(R.id.forecast_layout) as LinearLayout
        swipeRefresh = findViewById(R.id.swipe_refresh) as SwipeRefreshLayout
        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        navView = findViewById(R.id.nav_view) as NavigationView
        navView!!.setCheckedItem(R.id.nav_my_city)
        swipeRefresh!!.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary)
        nav_button.setImageResource(R.mipmap.category)
        nav_button.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                drawerLayout!!.openDrawer(GravityCompat.START)
            }
        })
        val cityTitle = findViewById(R.id.title_city) as TextView
        cityTitle.setOnClickListener(this)
        // 左边抽屉的选项；
        navView!!.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    // 我的城市；
                    R.id.nav_my_city -> {
                        navView!!.setCheckedItem(R.id.nav_my_city)
                        drawerLayout!!.closeDrawers()
                        getLocalCityWeatehrInfor()
                    }
                    // 特别关心；
                    R.id.nav_care -> {
                        navView!!.setCheckedItem(R.id.nav_care)
                        drawerLayout!!.closeDrawers()
                        startLoadFragmentActivity("nav_care")
                    }
                    // 切换城市；
                    R.id.nav_change_city -> {
                        navView!!.setCheckedItem(R.id.nav_change_city)
                        drawerLayout!!.closeDrawer(GravityCompat.START)
                        startLoadFragmentActivity("nav_change_city")
                    }
                    // 查找城市；
                    R.id.nav_find_city -> {
                        navView!!.setCheckedItem(R.id.nav_find_city)
                        drawerLayout!!.closeDrawers()
                        startLoadFragmentActivity("nav_find_city")
                    }
                    // 关于软件；
                    R.id.nav_about -> {
                        navView!!.setCheckedItem(R.id.nav_about)
                        drawerLayout!!.closeDrawers()
                        startLoadFragmentActivity("nav_about")
                    }
                }
                return true
            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.nav_add_care -> {
                LogUtil.e(TAG, mWeatherId!!)
                addToSave(mWeatherId!!)
                Toast.makeText(this,
                        ResourceUitls.getStringById(this, R.string.success_add),
                        Toast.LENGTH_SHORT).show()
                v.visibility = View.GONE
            }
            R.id.title_city -> {
                startLoadFragmentActivity("nav_care")
            }
            R.id.nav_share -> {
                if (WeChatUtils.getIWXAPIInstance(this).isWXAppInstalled) {
                    val filePath = ScreenUtils.saveBitmap(this,
                            ScreenUtils.compressBitmap(ScreenUtils.getBitmapByView(this, weatherLayout)))
                    WeChatUtils.getIWXAPIInstance(this)
                    DialogUtils.showShareDialog(this, filePath)
                } else {
                    Toast.makeText(this,
                            ResourceUitls.getStringById(this, R.string.share_error),
                            Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun addToSave(mWeatherId: String) {
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val stringBuffer = StringBuffer()
        stringBuffer.append(preference.getString("care_cities", ""))
        if (stringBuffer.isEmpty()) {
            stringBuffer.append(mWeatherId)
        } else {
            stringBuffer.append("&$mWeatherId")
        }
        val editor = preference.edit()
        editor.putString("care_cities", stringBuffer.toString())
        editor.apply()
    }

    private fun isVisiableToNavAddBtn(mWeatherId: String?): Boolean {
        var isVisiable = true
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val weatherIds = preference.getString("care_cities", null)
        if (weatherIds != null) {
            val weatherId = weatherIds.split("&")
            for (wid in weatherId) {
                if (wid == mWeatherId) {
                    isVisiable = false
                }
            }
        }
        return isVisiable
    }

    // 我的城市->获取当地城市天气信息；
    private fun getLocalCityWeatehrInfor() {
        weatherLayout!!.visibility = View.INVISIBLE
        swipeRefresh!!.isRefreshing = true
        GetLocalCityWeatherAsyncTask(this).execute()
    }

    // 获取我的城市的天气信息；
    class GetLocalCityWeatherAsyncTask() : AsyncTask<Void, Void, String>() {
        private val url = "https://api.map.baidu.com/location/ip?ak=MIKZkVGGXad6Y2FBaNQUISHSwN9trsol"
        private val TAG = "GetLocalCityWeatherAsyncTask"
        private var wa: WeakReference<WeatherActivity>? = null

        constructor(activity: WeatherActivity): this() {
            wa = WeakReference(activity)
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): String {
            val client = OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .build()
            val sb = StringBuilder()
            var result = ""
            val request = Request.Builder()
                    .url(url)
                    .build()
            try {
                val response = client.newCall(request).execute()
                val resp = response.body()!!.string()
                val respJson = JSONObject(resp)
                val contentJson = respJson.getJSONObject("content")
                val addressJson = contentJson.getJSONObject("address_detail")
                val province = addressJson.getString("province")
                val city = addressJson.getString("city")
                LogUtil.e(TAG, province + city)
                sb.append(city.substring(0, city.length - 1))
                for (countyBean in MainActivity.countyBeanList) {
                    if (countyBean.countyName == sb.toString()) {
                        result = countyBean.weatherId
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (wa != null) {
                val activity = wa!!.get()
                if (result != "") {
                    activity!!.requestWeather(result)
                } else {
                    Toast.makeText(activity, R.string.find_error_toast, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startLoadFragmentActivity(type: String) {
        val intent = Intent(this@WeatherActivity, LoadFragmentActivity::class.java)
        intent.putExtra("load_fragment", type)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 &&
                (resultCode == 1 || resultCode == 2 || resultCode == 3)) {
            weatherLayout!!.visibility = View.INVISIBLE
            swipeRefresh!!.isRefreshing = true
            mWeatherId = data!!.getStringExtra("weather_id")
            requestWeather(mWeatherId)
        }
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
        if (isExit == false) {
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