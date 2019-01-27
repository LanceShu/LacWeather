package com.example.xiyou3g.lacweather.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.util.HttpUtil;
import com.example.xiyou3g.lacweather.util.IconUtils;
import com.example.xiyou3g.lacweather.util.LogUtil;
import com.example.xiyou3g.lacweather.util.ThreadPoolUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Lance
 * on 2019/1/11.
 */

public class SplashActivity extends AppCompatActivity{
    private static final String TAG = "SplashActivity";
    private static final String image_url = "http://guolin.tech/api/bing_pic";
    public static final int UPDATE_SPLASH_BACK = 1;
    public final int DURATION = 300;
    public final int START_TIME = 2500;
    public int currentTime = 0;
    private SplashHandler splashHandler;
    private SharedPreferences preferences;

    private ImageView splashImage;
    private TextView[] tvs = new TextView[5];

    static class SplashHandler extends Handler {
        private WeakReference<Activity> activity;
        private WeakReference<View> view;

        SplashHandler (Activity activity, View view) {
            this.activity = new WeakReference<>(activity);
            this.view = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            if (activity != null && view != null) {
                final SplashActivity a = (SplashActivity) activity.get();
                View v = view.get();
                switch (msg.what) {
                    case UPDATE_SPLASH_BACK:
                        if (v instanceof ImageView) {
                            Log.e(TAG, "handler");
                            String url = (String) msg.obj;
                            Log.e(TAG, url);
                            if (!a.isFinishing()) {
                                Glide.with(a).load(url).into((ImageView) v);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_layout);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentTime = (int) System.currentTimeMillis();
        // 初始化数据;
        initData();
        // 初始化控件;
        initWight();
        // 检查权限;
        CheckPermission();
    }

    private void initData() {
        IconUtils.setWeatherBlackIcons();
        IconUtils.setWeatherWhiteIcons();
    }

    private void CheckPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) !=PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionList.isEmpty()) {
            /*如果不需要请求权限就执行操作*/
            loadBack();
        } else {
            String[] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permission, 1);
        }
    }

    // 加载背景；
    private void loadBack() {
        splashHandler = new SplashHandler(this, splashImage);
        setAlphaAnimByView(tvs[0], 0.0f, 1.0f, 0, DURATION);
        setAlphaAnimByView(tvs[1], 0.0f, 1.0f, 300, DURATION);
        setAlphaAnimByView(tvs[2], 0.0f, 1.0f, 600, DURATION);
        setAlphaAnimByView(tvs[3], 0.0f, 1.0f, 900, DURATION);
        setAlphaAnimByView(tvs[4], 0.0f, 1.0f, 1200, DURATION);
        ThreadPoolUtils.getInstance().excute(new Runnable() {
            @Override
            public void run() {
                loadImageBackground();
            }
        });
        currentTime = (int)System.currentTimeMillis() - currentTime;
        if (currentTime >= START_TIME) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            splashHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, START_TIME);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        //权限若没有全部通过，就结束Demo
                        finish();
                        break;
                    }
                }
                /*请求权限成功后执行操作*/
                loadBack();
            } else {
                finish();
            }
        }
    }

    private void initWight() {
        splashImage = (ImageView) findViewById(R.id.splash_background);
        splashImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tvs[0] = (TextView) findViewById(R.id.splash_tv_1);
        tvs[1] = (TextView) findViewById(R.id.splash_tv_2);
        tvs[2] = (TextView) findViewById(R.id.splash_tv_3);
        tvs[3] = (TextView) findViewById(R.id.splash_tv_4);
        tvs[4] = (TextView) findViewById(R.id.splash_tv_5);
    }

    private void setAlphaAnimByView(View view, float fromAlpha, float toAlpha,
                                    int offStart, int duration) {
        view.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setStartOffset(offStart);
        alphaAnimation.setDuration(duration);
        view.setAnimation(alphaAnimation);
    }

    // 网络请求加载背景图；
    private void loadImageBackground() {
        HttpUtil.INSTANCE.sendOkHttpRequest(image_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.INSTANCE.e(TAG, e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this,
                                "请检查网络连接",
                                Toast.LENGTH_SHORT).show();
                        getInforFromSharedPreference();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp != null) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("splash_back", resp);
                    editor.apply();
                    Message message = Message.obtain();
                    message.obj = resp;
                    message.what = UPDATE_SPLASH_BACK;
                    splashHandler.sendMessage(message);
                }
            }
        });
    }

    // 从缓存中获取背景图信息;
    private boolean getInforFromSharedPreference(){
        boolean flag = false;
        String url = preferences.getString("splash_back", null);
        if (url != null) {
            flag = true;
            Message message = Message.obtain();
            message.obj = url;
            message.what = UPDATE_SPLASH_BACK;
            splashHandler.sendMessage(message);
        }
        return flag;
    }

    @Override
    protected void onDestroy() {
        splashHandler.removeCallbacksAndMessages(0);
        super.onDestroy();
    }
}
