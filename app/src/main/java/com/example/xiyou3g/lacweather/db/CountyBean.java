package com.example.xiyou3g.lacweather.db;

/**
 * Created by Lance
 * on 2019/1/13.
 */

public class CountyBean {
    public int weatherId;
    public String countyName;
    public CountyBean(int weatherId, String countyName) {
        this.weatherId = weatherId;
        this.countyName = countyName;
    }
}
