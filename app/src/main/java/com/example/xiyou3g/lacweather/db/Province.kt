package com.example.xiyou3g.lacweather.db

import org.litepal.crud.DataSupport

/**
 * Created by Lance on 2017/8/15.
 */

class Province : DataSupport() {

    var provinceId: Int = 0
    var provinceName: String? = null  /*省的名字*/
    var provinceCode: Int = 0         /*省的代号*/
}
