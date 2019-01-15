package com.example.xiyou3g.lacweather.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.fragment.ChooseAreaFragment;
import com.example.xiyou3g.lacweather.util.LogUtil;

/**
 * Created by Lance
 * on 2019/1/15.
 */

public class LoadFragmentActivity extends AppCompatActivity
        implements ChooseAreaFragment.OnSetBackListener {
    private static final int layoutId = R.id.load_fragment;
    private static final String TAG = "LoadFragmentActivity";
    private ChooseAreaFragment chooseAreaFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_load_fragment);
        // 初始化控件;
        initWight();
    }

    private void initWight() {
        switch (getIntent().getStringExtra("load_fragment")) {
            case "change_city":
                chooseAreaFragment = new ChooseAreaFragment();
                chooseAreaFragment.setBackListener(this);
                addFragment(layoutId, chooseAreaFragment);
                break;
            default:
                break;
        }
    }

    private void addFragment(int layoutId, Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(layoutId, fragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (chooseAreaFragment.getCurrentLevel() == 0) {
                finish();
            } else {
                back();
            }
        }
        return false;
    }

    @Override
    public void back() {
        chooseAreaFragment.backToLast();
    }
}
