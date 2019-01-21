package com.example.xiyou3g.lacweather.asynctask;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiyou3g.lacweather.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Lance
 * on 2019/1/16.
 */

public class GetLocalCityAsyncTask extends AsyncTask<Void, Void, String> {
    private static final String url = "https://api.map.baidu.com/location/ip?ak=MIKZkVGGXad6Y2FBaNQUISHSwN9trsol";
    private static final String TAG = "GetLocalCityAsyncTask";
    private WeakReference<ImageView> im;
    private WeakReference<TextView> tv;
    private WeakReference<GifImageView> gfv;
    private WeakReference<ImageView> localIv;
    private String currentCity;

    public GetLocalCityAsyncTask(ImageView imageView, TextView textView, GifImageView gifImageView) {
        im = new WeakReference<>(imageView);
        tv = new WeakReference<>(textView);
        gfv = new WeakReference<>(gifImageView);
    }

    public GetLocalCityAsyncTask(ImageView imageView, String cityName) {
        localIv = new WeakReference<>(imageView);
        currentCity = cityName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (im != null && tv != null && gfv != null) {
            ImageView iconImage = im.get();
            TextView nameView = tv.get();
            GifImageView gifView = gfv.get();
            iconImage.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
            gifView.setVisibility(View.VISIBLE);
        }
        if (localIv != null) {
            ImageView localIcon = localIv.get();
            localIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        StringBuilder sb = new StringBuilder();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String resp = response.body().string();
            JSONObject respJson = new JSONObject(resp);
            JSONObject contentJson = respJson.getJSONObject("content");
            JSONObject addressJson = contentJson.getJSONObject("address_detail");
            String province = addressJson.getString("province");
            String city = addressJson.getString("city");
            sb.append(city.substring(0, city.length() - 1));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        if (im != null && tv != null && gfv != null) {
            ImageView iconImage = im.get();
            TextView nameView = tv.get();
            GifImageView gifView = gfv.get();
            gifView.setVisibility(View.GONE);
            iconImage.setVisibility(View.VISIBLE);
            nameView.setVisibility(View.VISIBLE);
            nameView.setText(string);
        }
        if (localIv != null) {
            ImageView localIcon = localIv.get();
            if (string.equals(currentCity)) {
                localIcon.setVisibility(View.VISIBLE);
            }
        }
    }
}
