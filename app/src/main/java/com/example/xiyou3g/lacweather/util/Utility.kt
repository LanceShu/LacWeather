package com.example.xiyou3g.lacweather.util

import android.text.TextUtils
import com.example.xiyou3g.lacweather.db.City
import com.example.xiyou3g.lacweather.db.County
import com.example.xiyou3g.lacweather.db.Province
import com.example.xiyou3g.lacweather.gson.Weather
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Lance on 2017/8/15.
 */

object Utility{

    /*解析与处理服务器返回的省级数据*/
    fun handleProvinceRespose(response: String): Boolean{
        if(response.length != 173){
            val allProvince: JSONArray = JSONArray(response)
            for(i in 0..allProvince.length()-1){
                val provinceObject: JSONObject = allProvince.getJSONObject(i)
                val province: Province = Province()
                province.provinceName = provinceObject.getString("name")
                province.provinceCode = provinceObject.getInt("id")
                province.save()
            }
            return true
        }
        return false
    }

    /*解析与处理服务器返回的市级数据*/
    fun handleCityResponse(response: String,provinceCode: Int): Boolean{
        LogUtil.e("cityresponse",response.length.toString())
        if(response.length != 173){
            val allCities: JSONArray = JSONArray(response)
            for(i in 0..allCities.length()-1){
                val cityObeject: JSONObject = allCities.getJSONObject(i)
                val city = City()
                city.cityName = cityObeject.getString("name")
                city.cityCode = cityObeject.getInt("id")
                city.provinceId = provinceCode
                city.save()
            }
            return true
        }
        return false
    }

    /*解析与处理服务器返回的县级数据*/
    fun handleCountResponse(response: String,cityCode: Int): Boolean{
        if(response.length != 173){
            val allCounties: JSONArray = JSONArray(response)
            for(i in  0..allCounties.length()-1){
                val countyObject: JSONObject = allCounties.getJSONObject(i)
                val county: County = County()
                county.countyName = countyObject.getString("name")
                county.weatherId = countyObject.getString("weather_id")
                county.cityId = cityCode
                county.save()
            }
            return true
        }
        return false
    }

    fun handleWeatherResponse(response: String): Weather? {
        LogUtil.e("handleresponse",response.length.toString())
        if(response.length > 200){
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("HeWeather")
            val weatherContent = jsonArray.getJSONObject(0).toString()
            return Gson().fromJson(weatherContent,Weather::class.java)
        }
        return null
    }

}