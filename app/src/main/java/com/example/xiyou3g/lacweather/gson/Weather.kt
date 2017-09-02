package com.example.xiyou3g.lacweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Lance on 2017/8/17.
 */

class Weather{

    var status: String? = null
    var basic: Basic? = null
    var aqi: AQI? = null
    var now: Now? = null
    var suggestion: Suggestion? = null

    @SerializedName("daily_forecast")
    var forecastList: List<Forecast>? = null
}