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
import com.example.xiyou3g.lacweather.fragment.FindCityFragment;

/**
 * Created by Lance
 * on 2019/1/15.
 */

public class LoadFragmentActivity extends AppCompatActivity
        implements ChooseAreaFragment.OnChooseSetBackListener {
    private static final int layoutId = R.id.load_fragment;
    private static final String TAG = "LoadFragmentActivity";
    private ChooseAreaFragment chooseAreaFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_load_fragment);
        // 初始化控件;
        initWight();
    }

    private void initWight() {
        switch (getIntent().getStringExtra("load_fragment")) {
            case "building_icon":
                chooseAreaFragment = new ChooseAreaFragment();
                chooseAreaFragment.setBackListener(this);
                addFragment(layoutId, chooseAreaFragment);
                currentFragment = chooseAreaFragment;
                break;
            case "nav_find_city":
                FindCityFragment findCityFragment = new FindCityFragment();
                addFragment(layoutId, findCityFragment);
                currentFragment = findCityFragment;
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
            if (currentFragment instanceof ChooseAreaFragment) {
                if (chooseAreaFragment.getCurrentLevel() == 0) {
                    finish();
                } else {
                    chooseBack();
                }
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    public void chooseBack() {
        chooseAreaFragment.backToLast();
    }
}
