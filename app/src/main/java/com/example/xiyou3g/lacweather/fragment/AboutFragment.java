package com.example.xiyou3g.lacweather.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.util.ApkUtils;
import com.example.xiyou3g.lacweather.util.LogUtil;
import com.example.xiyou3g.lacweather.util.ResourceUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2019/1/25.
 */

public class AboutFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AboutFragment";
    private static final String VERSION_CODE_URL = "http://123.207.145.251:8080/LacWeather/checkout_code";
    private static final String GET_APK_UPGRADE_URL = "http://123.207.145.251:8080/LacWeather/get_upgrade_url";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_app_fragment, container, false);
        // 初始化控件;
        initWight(view);
        return view;
    }

    private void initWight(View view) {
        TextView aboutTitle = (TextView) view.findViewById(R.id.title_city);
        aboutTitle.setText(ResourceUitls.getStringById(getContext(), R.string.menu_about));
        ImageView backBtn = (ImageView) view.findViewById(R.id.nav_button);
        backBtn.setOnClickListener(this);
        ImageView checkout = (ImageView) view.findViewById(R.id.about_checkout_upgrade);
        checkout.setOnClickListener(this);
        TextView version = (TextView) view.findViewById(R.id.about_app_version);
        String currentVersion = ApkUtils.getAPKVersionName(getContext(),
                getContext().getPackageName());
        if (currentVersion != null) {
            String v = ResourceUitls.getStringById(getContext(), R.string.app_version) + currentVersion;
            version.setText(v);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_button:
                getActivity().finish();
                break;
            case R.id.about_checkout_upgrade:
                checkoutVersionCode();
                break;
            default:
                break;
        }
    }

    private void checkoutVersionCode() {
        int version = ApkUtils.getAPKVersionCode(getContext(),
                getContext().getPackageName());
        if (version != -1) {
            new CheckoutVersionCodeTask(getContext(), version).execute(VERSION_CODE_URL);
        }
    }

    // 检查更新任务；
    static class CheckoutVersionCodeTask extends AsyncTask<String, Void, Integer> {
        private WeakReference<Context> c;
        private int localVersionCode;
        private ProgressDialog progressDialog;

        CheckoutVersionCodeTask(Context context, int localVersionCode) {
            c = new WeakReference<>(context);
            this.localVersionCode = localVersionCode;
        }

        @Override
        protected void onPreExecute() {
            if (c != null) {
                Context context = c.get();
                progressDialog = createProgressDialog(context);
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            String url = strings[0];
            int versionCode = -1;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response != null) {
                    String resp = response.body().string();
                    if (!resp.contains("file is not exists!")) {
                        JSONObject jsonObject = new JSONArray(resp).getJSONObject(0);
                        JSONObject apkInfo = jsonObject.getJSONObject("apkInfo");
                        versionCode = apkInfo.getInt("versionCode");
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return versionCode;
        }

        @Override
        protected void onPostExecute(Integer currentVersionCode) {
            if (c != null) {
                Context context = c.get();
                LogUtil.INSTANCE.e(TAG, "currentCode:" + localVersionCode
                        + ", newVersionCode:" + currentVersionCode);
                if (currentVersionCode > localVersionCode) {
                    // 展示下载询问框;
                    showAlertDialog(context);
                } else {
                    Toast.makeText(context,
                            ResourceUitls.getStringById(context, R.string.app_name) + "已是最新",
                            Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }

        private ProgressDialog createProgressDialog(Context context) {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(ResourceUitls.getStringById(context, R.string.dialog_title));
            progressDialog.setMessage("正在检查更新...");
            progressDialog.create();
            return progressDialog;
        }

        private void showAlertDialog(final Context context) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle(ResourceUitls.getStringById(context, R.string.dialog_title));
            alertDialog.setMessage("有新版本，是否立即更新?");
            alertDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadApkFromServerTask(context).execute(GET_APK_UPGRADE_URL);
                    Toast.makeText(context, "后台开始下载", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.setNegativeButton("以后再说", null);
            alertDialog.show();
        }
    }

    // apk下载任务；
    static class DownloadApkFromServerTask extends AsyncTask<String, Void, Void> {
        private WeakReference<Context> c;

        DownloadApkFromServerTask(Context context) {
            c = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (c != null) {
                Context context = c.get();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response != null) {
                        String resp = response.body().string();
                        if (!resp.contains("file is not exists!")) {
                            ApkUtils.downloadAPK(context, resp);
                            LogUtil.INSTANCE.e(TAG, resp);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
