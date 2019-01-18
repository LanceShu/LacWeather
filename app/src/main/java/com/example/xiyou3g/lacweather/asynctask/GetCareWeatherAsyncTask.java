package com.example.xiyou3g.lacweather.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.xiyou3g.lacweather.activity.LoadFragmentActivity;
import com.example.xiyou3g.lacweather.db.CareBean;
import com.example.xiyou3g.lacweather.fragment.CareCitiesFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2019/1/18.
 */

public class GetCareWeatherAsyncTask extends AsyncTask<String, Void, List<CareBean>> {
    private static final String TAG = "GetCareWeatherAsyncTask";
    private WeakReference<CareCitiesFragment> ccf;
    private WeakReference<LoadFragmentActivity> lfa;
    private ProgressDialog dialog;

    public GetCareWeatherAsyncTask(CareCitiesFragment careCitiesFragment,
                                   LoadFragmentActivity loadFragmentActivity) {
        ccf = new WeakReference<>(careCitiesFragment);
        lfa = new WeakReference<>(loadFragmentActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (lfa != null) {
            LoadFragmentActivity activity = lfa.get();
            dialog = createProgressDialog(activity);
        }
        if (dialog != null) {
            showProgressDialog(dialog);
        }
    }

    @Override
    protected List<CareBean> doInBackground(String... strings) {
        List<CareBean> careBeanList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        String[] weatherIds = strings[0].contains("&") ?
                strings[0].split("&") : new String[]{strings[0]};
        for (String weathId : weatherIds) {
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weathId + "&key=710e976d83e54ad9b139b42267ba4ce7";
            Request request = new Request.Builder()
                    .url(weatherUrl)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String resp = response.body().string();
                CareBean careBean = new CareBean();
                JSONObject heWeatherJson = new JSONObject(resp)
                        .getJSONArray("HeWeather")
                        .getJSONObject(0);
                JSONObject basicJson = heWeatherJson.getJSONObject("basic");
                careBean.setCountyName(basicJson.getString("parent_city") + " · "
                        +basicJson.getString("location"));
                JSONObject nowJson = heWeatherJson.getJSONObject("now");
                careBean.setCountyWeather(nowJson.getString("cond_txt"));
                JSONObject dailyJson = heWeatherJson.getJSONArray("daily_forecast")
                        .getJSONObject(0).getJSONObject("tmp");
                careBean.setCountyTemp(dailyJson.getString("min") + " ℃ ~ "
                        + dailyJson.getString("max") + " ℃");
                careBean.setCountyWeatherId(weathId);
                careBeanList.add(careBean);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return careBeanList;
    }

    @Override
    protected void onPostExecute(List<CareBean> careBeans) {
        super.onPostExecute(careBeans);
        if (ccf != null) {
            CareCitiesFragment careCitiesFragment = ccf.get();
            dismissProgressDialog(dialog);
            for (CareBean careBean : careBeans) {
                careCitiesFragment.addDataToCareList(careBean);
            }
            careCitiesFragment.notifyDataChanged();
        }
    }

    // 创建加载框；
    private ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("温馨提示：");
        dialog.setMessage("正在加载...");
        dialog.setCancelable(false);
        dialog.create();
        return dialog;
    }

    // 展示加载框；
    private void showProgressDialog(ProgressDialog dialog) {
        dialog.show();
    }

    // 取消加载框；
    private void dismissProgressDialog(ProgressDialog dialog) {
        dialog.dismiss();
    }
}
