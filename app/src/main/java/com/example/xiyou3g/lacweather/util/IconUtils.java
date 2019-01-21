package com.example.xiyou3g.lacweather.util;

import com.example.xiyou3g.lacweather.R;

import java.util.HashMap;

/**
 * Created by Lance
 * on 2019/1/18.
 */

public class IconUtils {
    private static HashMap<String, Integer> weatherBlackIconsMap = new HashMap<>();
    private static HashMap<String, Integer> weatherWhiteIconsMap = new HashMap<>();

    public static void setWeatherBlackIcons() {
        weatherBlackIconsMap.put("暴雪", R.mipmap.wh_baoxue);
        weatherBlackIconsMap.put("暴雨", R.mipmap.wh_baoyu);
        weatherBlackIconsMap.put("冰雹", R.mipmap.wh_bingbao);
        weatherBlackIconsMap.put("多云", R.mipmap.wh_duoyun);
        weatherBlackIconsMap.put("风", R.mipmap.wh_feng);
        weatherBlackIconsMap.put("雷电", R.mipmap.wh_leidian);
        weatherBlackIconsMap.put("霾", R.mipmap.wh_mai);
        weatherBlackIconsMap.put("晴", R.mipmap.wh_qing);
        weatherBlackIconsMap.put("其他", R.mipmap.wh_qita);
        weatherBlackIconsMap.put("雾", R.mipmap.wh_wu);
        weatherBlackIconsMap.put("小雪", R.mipmap.wh_xiaoxue);
        weatherBlackIconsMap.put("小雨", R.mipmap.wh_xiaoyu);
        weatherBlackIconsMap.put("阴", R.mipmap.wh_yin);
        weatherBlackIconsMap.put("雨雪", R.mipmap.wh_yuxue);
        weatherBlackIconsMap.put("阵雪", R.mipmap.wh_zhenxue);
        weatherBlackIconsMap.put("阵雨", R.mipmap.wh_zhenyu);
        weatherBlackIconsMap.put("中雪", R.mipmap.wh_zhongxue);
        weatherBlackIconsMap.put("中雨", R.mipmap.wh_zhongyu);
    }

    public static void setWeatherWhiteIcons() {
        weatherWhiteIconsMap.put("暴雪", R.mipmap.wb_baoxue);
        weatherWhiteIconsMap.put("暴雨", R.mipmap.wb_baoyu);
        weatherWhiteIconsMap.put("冰雹", R.mipmap.wb_bingbao);
        weatherWhiteIconsMap.put("多云", R.mipmap.wb_duoyun);
        weatherWhiteIconsMap.put("风", R.mipmap.wb_feng);
        weatherWhiteIconsMap.put("雷电", R.mipmap.wb_leidian);
        weatherWhiteIconsMap.put("霾", R.mipmap.wb_mai);
        weatherWhiteIconsMap.put("晴", R.mipmap.wb_qing);
        weatherWhiteIconsMap.put("其他", R.mipmap.wb_qita);
        weatherWhiteIconsMap.put("雾", R.mipmap.wb_wu);
        weatherWhiteIconsMap.put("小雪", R.mipmap.wb_xiaoxue);
        weatherWhiteIconsMap.put("小雨", R.mipmap.wb_xiaoyu);
        weatherWhiteIconsMap.put("阴", R.mipmap.wb_yin);
        weatherWhiteIconsMap.put("雨雪", R.mipmap.wb_yuxue);
        weatherWhiteIconsMap.put("阵雪", R.mipmap.wb_zhenxue);
        weatherWhiteIconsMap.put("阵雨", R.mipmap.wb_zhenyu);
        weatherWhiteIconsMap.put("中雪", R.mipmap.wb_zhongxue);
        weatherWhiteIconsMap.put("中雨", R.mipmap.wb_zhongyu);
    }

    public static int getWeatherBlackIconByWeather(String weather) {
        if (weatherBlackIconsMap.containsKey(weather)) {
            return weatherBlackIconsMap.get(weather);
        } else {
            return weatherBlackIconsMap.get("其他");
        }
    }

    public static int getWeatherWhiteIconByWeather(String weather) {
        if (weatherWhiteIconsMap.containsKey(weather)) {
            return weatherWhiteIconsMap.get(weather);
        } else {
            return weatherWhiteIconsMap.get("其他");
        }
    }
}
