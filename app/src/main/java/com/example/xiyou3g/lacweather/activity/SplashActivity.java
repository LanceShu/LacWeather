package com.example.xiyou3g.lacweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.util.HttpUtil;
import com.example.xiyou3g.lacweather.util.LogUtil;
import com.example.xiyou3g.lacweather.util.ThreadPoolUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

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
        private WeakReference<TextView[]> textViews;

        SplashHandler (Activity activity, View view, TextView[] textViews) {
            this.activity = new WeakReference<>(activity);
            this.view = new WeakReference<>(view);
            this.textViews = new WeakReference<>(textViews);
        }

        @Override
        public void handleMessage(Message msg) {
            if (activity != null && view != null && textViews != null) {
                final SplashActivity a = (SplashActivity) activity.get();
                View v = view.get();
                final TextView[] tvs = textViews.get();
                switch (msg.what) {
                    case UPDATE_SPLASH_BACK:
                        if (v instanceof ImageView) {
                            Log.e(TAG, "handler");
                            String url = (String) msg.obj;
                            Log.e(TAG, url);
                            Glide.with(a).load(url).listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            e.printStackTrace();
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            a.setAlphaAnimByView(tvs[0], 0.0f, 1.0f, 0, a.DURATION);
                                            a.setAlphaAnimByView(tvs[1], 0.0f, 1.0f, 300, a.DURATION);
                                            a.setAlphaAnimByView(tvs[2], 0.0f, 1.0f, 600, a.DURATION);
                                            a.setAlphaAnimByView(tvs[3], 0.0f, 1.0f, 900, a.DURATION);
                                            a.setAlphaAnimByView(tvs[4], 0.0f, 1.0f, 1200, a.DURATION);
                                            a.currentTime = (int)System.currentTimeMillis() - a.currentTime;
                                            if (a.currentTime >= a.START_TIME) {
                                                a.startActivity(new Intent(a, MainActivity.class));
                                                a.finish();
                                            } else {
                                                postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        a.startActivity(new Intent(a, MainActivity.class));
                                                        a.finish();
                                                    }
                                                }, a.START_TIME);
                                            }
                                            return false;
                                        }
                                    })
                                    .into((ImageView) v);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_layout);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentTime = (int) System.currentTimeMillis();
        initWight();
        splashHandler = new SplashHandler(this, splashImage, tvs);
        ThreadPoolUtils.getInstance().excute(new Runnable() {
            @Override
            public void run() {
                loadImageBackground();
            }
        });
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

    private void loadImageBackground() {
        HttpUtil.INSTANCE.sendOkHttpRequest(image_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.INSTANCE.e(TAG, e.getCause().toString());
                if (e.getCause().equals(SocketTimeoutException.class)) {
                    if (!getInforFromSharedPreference()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SplashActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    getInforFromSharedPreference();
                }
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
