package com.example.xiyou3g.lacweather.db

import org.litepal.crud.DataSupport

/**
 * Created by Lance
 * on 2017/8/15.
 */

class City : DataSupport(){
    var cityId : Int = 0
    var cityName : String? = null   /*市的名字*/
    var cityCode :Int = 0           /*市的代号*/
    var provinceId : Int = 0        /*记录当前市所属的省的ID值*/
}

