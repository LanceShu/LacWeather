package com.example.xiyou3g.lacweather.util;

import android.util.Log;

import com.example.xiyou3g.lacweather.db.CountyBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2019/1/13.
 */

public class SplashUtil {
    private static List<CountyBean> countyBeans;

    public SplashUtil() {
        countyBeans = new ArrayList<>();
        getAllCountyList();
    }

    public static List<CountyBean> getAllCountyBeans() {
        return countyBeans;
    }

    private void getAllCountyList() {
        final String provinceAddress = "http://guolin.tech/api/china";
        HttpUtil.INSTANCE.sendOkHttpRequest(provinceAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp != null) {
                    try {
                        JSONArray allCountyArray = new JSONArray(resp);
                        for (int i = 0; i < allCountyArray.length(); i++) {
                            JSONObject countyObject = allCountyArray.getJSONObject(i);
                            int provinceId = countyObject.getInt("id");
                            getAllCity(provinceAddress, provinceId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getAllCity(String provinceAddress, int provinceId) {
        final String cityAddress = provinceAddress + "/" + provinceId;
        HttpUtil.INSTANCE.sendOkHttpRequest(cityAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp != null) {
                    try {
                        JSONArray allCountyArray = new JSONArray(resp);
                        for (int i = 0; i < allCountyArray.length(); i++) {
                            JSONObject countyObject = allCountyArray.getJSONObject(i);
                            int cityId = countyObject.getInt("id");
                            getAllCounty(cityAddress, cityId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getAllCounty(String cityAddress, int cityId) {
        String countyAddress = cityAddress + "/" + cityId;
        HttpUtil.INSTANCE.sendOkHttpRequest(countyAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp != null) {
                    try {
                        JSONArray allCountyArray = new JSONArray(resp);
                        for (int i = 0; i < allCountyArray.length(); i++) {
                            JSONObject countyObject = allCountyArray.getJSONObject(i);
                            int weatherId = countyObject.getInt("weather_id");
                            String countyName = countyObject.getString("name");
                            CountyBean countyBean = new CountyBean(weatherId, countyName);
                            countyBeans.add(countyBean);
                            Log.e("SplashUtil", countyName + "  " + weatherId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
