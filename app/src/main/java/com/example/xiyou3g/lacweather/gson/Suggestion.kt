package com.example.xiyou3g.lacweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Lance on 2017/8/17.
 */

class Suggestion{

    @SerializedName("comf")
    var comfort: Comfort? = null

    @SerializedName("cw")
    var carWash: CarWash? = null

    @SerializedName("sport")
    var sport: Sport? = null

    inner class Comfort{
        @SerializedName("txt")
        var info: String? = null
    }

    inner class CarWash{
        @SerializedName("txt")
        var info: String? = null
    }

    inner class Sport{
        @SerializedName("txt")
        var info: String? = null
    }
}