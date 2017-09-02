package com.example.xiyou3g.lacweather.gson

/**
 * Created by Lance on 2017/8/17.
 */

class AQI{

    var city: AQICity? = null

    inner class AQICity{
        var aqi: String? = null
        var pm25: String? = null
    }
}
