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
import com.example.xiyou3g.lacweather.fragment.ChooseAreaFragment;
import com.example.xiyou3g.lacweather.util.ResourceUitls;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lance
 * on 2019/1/14.
 */

public class MainActivity extends AppCompatActivity {
    private final static String provinceAddress = "http://guolin.tech/api/china";
    private LinearLayout forecastLayout;
    private Boolean isExit = false;
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        initWight();
        new GetCountyAsyncTask(this).execute(provinceAddress);
        if(prefs.getString("weather",null) != null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initWight() {
        addFragment(R.id.choose_area_fragment, new ChooseAreaFragment());
    }

    static class GetCountyAsyncTask extends AsyncTask<String, Integer, String> {
        private WeakReference<Context> c;
        private ProgressDialog dialog;

        GetCountyAsyncTask(Context context) {
            c = new WeakReference<>(context);
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
            int i = 0;
            for (;;) {
                if (i == 100) break;
                i ++;
                publishProgress(i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "success";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (c != null) {
                Context context = c.get();
                updateDialog(context, values[0]);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success")) {
                dialog.dismiss();
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
            exitClick();
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
