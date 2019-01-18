package com.example.xiyou3g.lacweather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.adapter.CareCityAdapter;
import com.example.xiyou3g.lacweather.db.CareBean;
import com.example.xiyou3g.lacweather.util.ResourceUitls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance
 * on 2019/1/18.
 */

public class CareCitiesFragment extends Fragment implements View.OnClickListener {
    private List<CareBean> careCitiesList;
    private CareCityAdapter careCityAdapter;

    public void addDataToCareList(CareBean careBean) {
        careCitiesList.add(careBean);
    }

    public void notifyDataChanged() {
        careCityAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        careCitiesList = new ArrayList<>();
        careCitiesList.clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.care_cities_fragment, container, false);
        initWight(view);
        return view;
    }

    private void initWight(View view) {
        // 设置标题栏的背景；
        RelativeLayout titleBar = (RelativeLayout) view.findViewById(R.id.care_title);
        titleBar.setBackgroundResource(R.color.colorPrimary);
        // 设置标题;
        TextView careTitle = (TextView) view.findViewById(R.id.title_city);
        careTitle.setText(ResourceUitls.getStringById(getContext(), R.string.menu_care));
        // 设置返回按钮;
        ImageView backBtn = (ImageView) view.findViewById(R.id.nav_button);
        backBtn.setOnClickListener(this);
        RecyclerView citiesView = (RecyclerView) view.findViewById(R.id.care_cities_list);
        citiesView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        citiesView.setLayoutManager(manager);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        careCityAdapter = new CareCityAdapter(careCitiesList, getActivity(), preferences);
        citiesView.setAdapter(careCityAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_button:
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
