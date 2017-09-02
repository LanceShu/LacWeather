package com.example.xiyou3g.lacweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Lance on 2017/8/17.
 */

class Basic{

    @SerializedName("city")
    var cityName: String? = null

    @SerializedName("id")
    var weatherId: String? = null

    var update: Update? = null

    inner class Update{
        @SerializedName("loc")
        var updateTime: String? = null
    }

}