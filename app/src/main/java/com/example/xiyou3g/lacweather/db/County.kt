package com.example.xiyou3g.lacweather.db

import org.litepal.crud.DataSupport

/**
 * Created by Lance on 2017/8/15.
 */

class County : DataSupport(){
    var countyId : Int = 0
    var countyName : String? = null     /*县的名字*/
    var weatherId : String? = null      /*县所对应的天气的id*/
    var cityId :Int = 0                 /*当前的所属的市的ID值*/
}