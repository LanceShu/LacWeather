package com.example.xiyou3g.lacweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Lance on 2017/8/17.
 */

class Now{

    @SerializedName("tmp")
    var temperature: String? = null

    @SerializedName("cond")
    var more: More? = null

    inner class More{

        @SerializedName("txt")
        var info: String? = null
    }

}