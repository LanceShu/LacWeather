package com.example.xiyou3g.lacweather.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.activity.WeatherActivity;
import com.example.xiyou3g.lacweather.asynctask.FindCityAsyncTask;
import com.example.xiyou3g.lacweather.util.LogUtil;
import com.example.xiyou3g.lacweather.util.ResourceUitls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance
 * on 2019/1/15.
 */

public class FindCityFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "FindCityFragment";
    private TextView findError;
    private List<String> findCityList;
    private List<String> weatherIdList;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findCityList = new ArrayList<>();
        weatherIdList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_city_fragment, container, false);
        // 初始化控件;
        initWight(view);
        return view;
    }

    private void initWight(View view) {
        // 设置标题栏的背景；
        RelativeLayout titleBar = (RelativeLayout) view.findViewById(R.id.find_city_bar);
        titleBar.setBackgroundResource(R.color.colorPrimary);
        // 设置标题栏的题目；
        TextView findTitle = (TextView) view.findViewById(R.id.title_city);
        findTitle.setText(ResourceUitls.getStringById(getContext(), R.string.menu_find_city));
        // 设置标题栏的返回按钮；
        ImageView backBtn = (ImageView) view.findViewById(R.id.nav_button);
        backBtn.setOnClickListener(this);
        // 初始化搜索按钮：
        ImageView findIcon = (ImageView) view.findViewById(R.id.find_icon);
        findIcon.setOnClickListener(this);
        // 搜索城市失败的TextView；
        findError = (TextView) view.findViewById(R.id.find_error);
        // 初始化搜索到的城市的列表；
        ListView cityListView = (ListView) view.findViewById(R.id.find_city_list);
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_expandable_list_item_1, findCityList);
        cityListView.setAdapter(adapter);
        new FindCityAsyncTask(findError, findCityList, weatherIdList, adapter).execute("");
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getContext(), WeatherActivity.class);
                Intent intent = new Intent();
                intent.putExtra("weather_id", weatherIdList.get(position));
                intent.putExtra("nav_change", 1);
                getActivity().setResult(2, intent);
                getActivity().finish();
            }
        });
        // 初始化搜索栏；
        EditText editText = (EditText) view.findViewById(R.id.find_edit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new FindCityAsyncTask(findError, findCityList, weatherIdList, adapter).execute(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
