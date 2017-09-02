package com.example.xiyou3g.lacweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Lance on 2017/8/17.
 */

class Forecast{

    var date: String? = null

    @SerializedName("tmp")
    var temperature: Temperature? = null

    @SerializedName("cond")
    var more: More? = null

    inner class Temperature{
        var max: String? = null
        var min: String? = null
    }

    inner class More{
        @SerializedName("txt_d")
        var inf: String? = null
    }
}
