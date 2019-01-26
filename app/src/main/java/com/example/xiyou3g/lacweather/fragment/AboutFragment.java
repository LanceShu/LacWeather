package com.example.xiyou3g.lacweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.util.ApkUtils;
import com.example.xiyou3g.lacweather.util.LogUtil;
import com.example.xiyou3g.lacweather.util.ResourceUitls;

/**
 * Created by Lance
 * on 2019/1/25.
 */

public class AboutFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AboutFragment";

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_button:
                getActivity().finish();
                break;
            case R.id.about_checkout_upgrade:
                String version = ApkUtils.getAPKVersionCode(getContext(),
                        getContext().getPackageName());
                if (version != null) {
                    LogUtil.INSTANCE.e(TAG, version);
                }
                break;
            default:
                break;
        }
    }
}
