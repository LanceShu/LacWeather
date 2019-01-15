package com.example.xiyou3g.lacweather.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.db.CountyBean;
import com.example.xiyou3g.lacweather.fragment.ChooseAreaFragment;
import com.example.xiyou3g.lacweather.util.LogUtil;
import com.example.xiyou3g.lacweather.util.ResourceUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2019/1/14.
 */

public class MainActivity extends AppCompatActivity
        implements ChooseAreaFragment.OnChooseSetBackListener {
    private final static String provinceAddress = "http://guolin.tech/api/china";
    private LinearLayout forecastLayout;
    private Boolean isExit = false;
    private final static String TAG = "MainActivity";
    public static StringBuffer allCountyWeatherIdAndName;
    public static List<CountyBean> countyBeanList;
    private ChooseAreaFragment chooseAreaFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        allCountyWeatherIdAndName = new StringBuffer();
        countyBeanList = new ArrayList<>();
        chooseAreaFragment = new ChooseAreaFragment();
        chooseAreaFragment.setBackListener(this);
        initWight();
        if (prefs.getString("getAllCounty", null) == null) {
            new GetCountyAsyncTask(this, prefs)
                    .execute(provinceAddress);
        } else {
            String allCountyMessage = prefs.getString("getAllCounty", null);
            new GetCountyAsyncTask(this, null)
                    .handleCountyMessage(allCountyMessage);
        }
        if(prefs.getString("weather",null) != null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initWight() {
        addFragment(R.id.choose_area_fragment, chooseAreaFragment);
    }

    @Override
    public void chooseBack() {
        chooseAreaFragment.backToLast();
    }

    static class GetCountyAsyncTask extends AsyncTask<String, Integer, String> {
        private WeakReference<Context> c;
        private WeakReference<SharedPreferences> spf;
        private ProgressDialog dialog;
        private final int TOTAL_LENGTH = 46626;

        GetCountyAsyncTask(Context context, SharedPreferences sharedPreferences) {
            c = new WeakReference<>(context);
            spf = new WeakReference<>(sharedPreferences);
        }

        @Override
        protected void onPreExecute() {
            if (c != null) {
                Context context = c.get();
                showDialog(context,
                        getStringById(context, R.string.main_dialog_title),
                        getStringById(context, R.string.main_dialog_content) + "  0%");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request provinceRequest = new Request.Builder()
                    .url(provinceAddress)
                    .build();
            try {
                Response provinceResp = client.newCall(provinceRequest).execute();
                String proRespString = provinceResp.body().string();
                JSONArray provinceArray = new JSONArray(proRespString);
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject provinceObject = provinceArray.getJSONObject(i);
                    int provinceId = provinceObject.getInt("id");
                    String cityAddress = provinceAddress + "/" + provinceId;
                    Request cityRequest = new Request.Builder()
                            .url(cityAddress)
                            .build();
                    Response cityResp = client.newCall(cityRequest).execute();
                    String cityRespString = cityResp.body().string();
                    JSONArray cityArray = new JSONArray(cityRespString);
                    for (int j = 0; j < cityArray.length(); j++) {
                        JSONObject cityObject = cityArray.getJSONObject(j);
                        int cityId = cityObject.getInt("id");
                        String cityName = cityObject.getString("name");
                        String countyAddress = cityAddress + "/" + cityId;
                        Request countyRequest = new Request.Builder()
                                .url(countyAddress)
                                .build();
                        Response countyResp = client.newCall(countyRequest).execute();
                        String countyRespString = countyResp.body().string();
                        JSONArray countyArray = new JSONArray(countyRespString);
                        for (int k = 0; k < countyArray.length(); k++) {
                            JSONObject countyObject = countyArray.getJSONObject(k);
                            String weather_id = countyObject.getString("weather_id");
                            String countyName = countyObject.getString("name");
                            allCountyWeatherIdAndName.append(countyName + "&"
                                    + weather_id + "&" + cityName);
                            allCountyWeatherIdAndName.append("-");
                            publishProgress(allCountyWeatherIdAndName.toString().length());
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return allCountyWeatherIdAndName.toString();
        }

        // 刷新加载进度条;
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (c != null) {
                Context context = c.get();
                double progress = (double)values[0] / TOTAL_LENGTH * 100;
                updateDialog(context, (int)progress);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (spf != null) {
                // 进行数据缓存;
                SharedPreferences prefs = spf.get();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("getAllCounty", s);
                editor.apply();
                // 将自定义字符串数据进行处理、解析;
                handleCountyMessage(s);
                dialog.dismiss();
            }
        }

        private void handleCountyMessage(String s) {
            String[] countys = s.split("-");
            for (String county : countys) {
                String[] countyMessage = county.split("&");
                CountyBean countyBean = new CountyBean(countyMessage[0],
                        countyMessage[1], countyMessage[2]);
                countyBeanList.add(countyBean);
            }
        }

        private void showDialog(Context context, String title, String content) {
            dialog = new ProgressDialog(context);
            dialog.setTitle(title);
            dialog.setMessage(content);
            dialog.setCancelable(false);
            dialog.show();
        }

        private void updateDialog(Context context, int progress) {
            dialog.setMessage(getStringById(context, R.string.main_dialog_content) + "  " + progress + "%");
        }

        private String getStringById(Context context, int stringId) {
            return ResourceUitls.getStringById(context, stringId);
        }
    }

    private void addFragment(int layoutId, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(layoutId, fragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (chooseAreaFragment.getCurrentLevel() == 0) {
                exitClick();
            } else {
                chooseBack();
            }
        }
        return false;
    }

    private void exitClick(){
        Timer timer;
        if(!isExit){
            isExit = true;
            timer = new Timer();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        }else{
            finish();
        }
    }
}
