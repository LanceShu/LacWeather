package com.example.xiyou3g.lacweather.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.util.LogUtil;
import com.example.xiyou3g.lacweather.util.ResourceUitls;

/**
 * Created by Lance
 * on 2019/1/15.
 */

public class FindCityFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "FindCityFragment";
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.find_city_fragment, container, false);
        // 初始化控件;
        initWight(view);
        return view;
    }

    private void initWight(View view) {
        RelativeLayout titleBar = (RelativeLayout) view.findViewById(R.id.find_city_bar);
        titleBar.setBackgroundResource(R.color.colorPrimary);
        TextView findTitle = (TextView) view.findViewById(R.id.title_city);
        findTitle.setText(ResourceUitls.getStringById(context, R.string.menu_find_city));
        ImageView backBtn = (ImageView) view.findViewById(R.id.nav_button);
        backBtn.setOnClickListener(this);
        ImageView findIcon = (ImageView) view.findViewById(R.id.find_icon);
        findIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_button:
                getActivity().finish();
                break;
            case R.id.find_icon:
                break;
            default:
                break;
        }
    }
}
