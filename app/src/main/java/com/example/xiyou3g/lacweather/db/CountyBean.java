package com.example.xiyou3g.lacweather.db;

/**
 * Created by Lance
 * on 2019/1/13.
 */

public class CountyBean {
    public String weatherId;
    public String countyName;
    public String cityName;
    public CountyBean(String weatherId, String countyName, String cityName) {
        this.weatherId = weatherId;
        this.countyName = countyName;
        this.cityName = cityName;
    }
}
