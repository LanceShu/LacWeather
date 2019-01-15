package com.example.xiyou3g.lacweather.asynctask;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.xiyou3g.lacweather.activity.MainActivity;
import com.example.xiyou3g.lacweather.db.CountyBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance
 * on 2019/1/15.
 */

public class FindCityAsyncTask extends AsyncTask<String, Void, List<String>> {
    private WeakReference<List<String>> fcl;
    private WeakReference<List<String>> wil;
    private WeakReference<ArrayAdapter<String>> ad;
    private WeakReference<TextView> tv;

    public FindCityAsyncTask(TextView findError,
                             List<String> findCityList,
                             List<String> weatherIdList,
                             ArrayAdapter<String> adapter) {
        tv = new WeakReference<>(findError);
        fcl = new WeakReference<>(findCityList);
        wil = new WeakReference<>(weatherIdList);
        ad = new WeakReference<>(adapter);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> weatherIdList;
        weatherIdList = wil != null ? wil.get() : new ArrayList<String>();
        weatherIdList.clear();
        List<String> cityList = new ArrayList<>();
        String cityName = strings[0];
        for (CountyBean countyBean : MainActivity.countyBeanList) {
            if (countyBean.countyName.contains(cityName)) {
                String result = countyBean.cityName + " · " + countyBean.countyName;
                cityList.add(result);
                weatherIdList.add(countyBean.weatherId);
            }
        }
        return cityList;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        if (fcl != null && ad != null && tv != null) {
            List<String> cityList = fcl.get();
            ArrayAdapter<String> adapter = ad.get();
            TextView findError = tv.get();
            cityList.clear();
            if (strings.size() == 0) {
                findError.setVisibility(View.VISIBLE);
            } else {
                cityList.addAll(strings);
                findError.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
