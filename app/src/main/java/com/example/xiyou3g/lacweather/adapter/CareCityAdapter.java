package com.example.xiyou3g.lacweather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiyou3g.lacweather.R;
import com.example.xiyou3g.lacweather.asynctask.GetLocalCityAsyncTask;
import com.example.xiyou3g.lacweather.db.CareBean;
import com.example.xiyou3g.lacweather.util.IconUtils;
import com.example.xiyou3g.lacweather.util.LogUtil;

import java.util.List;

/**
 * Created by Lance
 * on 2019/1/18.
 */

public class CareCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<CareBean> careBeanList;
    private Activity activity;
    private SharedPreferences preferences;
    private TextView noCityText;

    public CareCityAdapter(List<CareBean> careBeanList,
                           Activity activity,
                           SharedPreferences preferences,
                           TextView noCityText) {
        this.careBeanList = careBeanList;
        this.activity = activity;
        this.preferences = preferences;
        this.noCityText = noCityText;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.care_city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        CareBean careBean = careBeanList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.cityName.setText(careBean.getCountyName());
        setLocalIconVisiable(viewHolder.cityLocalIcon, careBean.getCountyName());
        viewHolder.cityWeather.setText(careBean.getCountyWeather());
        getIconToImageView(viewHolder.cityWeatherIcon, careBean.getCountyWeather());
        viewHolder.cityTemp.setText(careBean.getCountyTemp());
        viewHolder.cityDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                careBeanList.remove(position);
                notifyDataSetChanged();
                SharedPreferences.Editor editor = preferences.edit();
                StringBuilder stringBuilder = new StringBuilder();
                if (careBeanList.size() == 0) {
                    editor.putString("care_cities", null);
                } else {
                    for (CareBean careBean1 : careBeanList) {
                        if (stringBuilder.toString().length() == 0) {
                            stringBuilder.append(careBean1.getCountyWeatherId());
                        } else {
                            stringBuilder.append("&" + careBean1.getCountyWeatherId());
                        }
                    }
                    editor.putString("care_cities", stringBuilder.toString());
                }
                editor.apply();
            }
        });
        viewHolder.careLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String weatherId = careBeanList.get(position).getCountyWeatherId();
                intent.putExtra("weather_id", weatherId);
                intent.putExtra("nav_change", 1);
                activity.setResult(3, intent);
                activity.finish();
            }
        });
    }

    private void setLocalIconVisiable(ImageView cityLocalIcon, String countyName) {
        String name = countyName.split("·")[1].trim();
        new GetLocalCityAsyncTask(cityLocalIcon, name).execute();
    }

    private void getIconToImageView(ImageView cityWeatherIcon, String countyWeather) {
        int iconId = IconUtils.getWeatherBlackIconByWeather(countyWeather);
        cityWeatherIcon.setImageResource(iconId);
    }

    @Override
    public int getItemCount() {
        noCityText.setVisibility(careBeanList.size() > 0 ? View.GONE : View.VISIBLE);
        return careBeanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityName;
        private ImageView cityLocalIcon;
        private TextView cityWeather;
        private ImageView cityWeatherIcon;
        private TextView cityTemp;
        private ImageView cityDelete;
        private ConstraintLayout careLayout;

        ViewHolder(View view) {
            super(view);
            careLayout = (ConstraintLayout) view.findViewById(R.id.care_layout);
            cityName = (TextView) view.findViewById(R.id.care_city_name);
            cityLocalIcon = (ImageView) view.findViewById(R.id.care_local_icon);
            cityWeather = (TextView) view.findViewById(R.id.care_city_weather);
            cityWeatherIcon = (ImageView) view.findViewById(R.id.care_city_weather_icon);
            cityTemp = (TextView) view.findViewById(R.id.care_city_temperature);
            cityDelete = (ImageView) view.findViewById(R.id.care_city_delete);
        }
    }
}
