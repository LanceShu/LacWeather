package com.example.xiyou3g.lacweather.db;

/**
 * Created by Lance
 * on 2019/1/18.
 */

public class CareBean {
    private String countyName;
    private String countyWeather;
    private String countyTemp;
    private String countyWeatherId;

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setCountyTemp(String countyTemp) {
        this.countyTemp = countyTemp;
    }

    public void setCountyWeather(String countyWeather) {
        this.countyWeather = countyWeather;
    }

    public void setCountyWeatherId(String countyWeatherId) {
        this.countyWeatherId = countyWeatherId;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getCountyTemp() {
        return countyTemp;
    }

    public String getCountyWeather() {
        return countyWeather;
    }

    public String getCountyWeatherId() {
        return countyWeatherId;
    }
}
