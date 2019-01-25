package com.example.xiyou3g.lacweather.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.xiyou3g.lacweather.R
import com.example.xiyou3g.lacweather.activity.LoadFragmentActivity
import com.example.xiyou3g.lacweather.activity.MainActivity
import com.example.xiyou3g.lacweather.activity.MainActivity.countyBeanList
import com.example.xiyou3g.lacweather.activity.WeatherActivity
import com.example.xiyou3g.lacweather.asynctask.GetLocalCityAsyncTask
import com.example.xiyou3g.lacweather.db.City
import com.example.xiyou3g.lacweather.db.County
import com.example.xiyou3g.lacweather.db.Province
import com.example.xiyou3g.lacweather.util.HttpUtil
import com.example.xiyou3g.lacweather.util.ResourceUitls
import com.example.xiyou3g.lacweather.util.ThreadPoolUtils
import com.example.xiyou3g.lacweather.util.Utility
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.crud.DataSupport
import pl.droidsonroids.gif.GifImageView
import java.io.IOException
import java.util.*

/**
 * Created by Lance
 * on 2017/8/16.
 */

class ChooseAreaFragment : Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.back_button -> {
                if (backListener != null) {
                    backListener!!.chooseBack()
                }
            }
            R.id.local_city_name -> {
                ThreadPoolUtils.getInstance().excute({
                    val cityName = localCityName!!.text
                    var weather_id: String? = null
                    for (countyBean in countyBeanList){
                        if (countyBean!!.countyName == cityName) {
                            weather_id = countyBean.weatherId
                        }
                    }
                    if (weather_id != null) {
                        activity.runOnUiThread {
                            setResultToWeatherActivity(weather_id)
                        }
                    } else {
                        activity.runOnUiThread {
                            Toast.makeText(activity,
                                    ResourceUitls.getStringById(activity, R.string.find_error_toast),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }

    fun setBackListener(backListener: OnChooseSetBackListener) {
        this.backListener = backListener
    }

    interface OnChooseSetBackListener {
        fun chooseBack()
    }

    private var progressDialog: ProgressDialog? = null
    private var titleText: TextView? = null
    private var backButton: ImageView? = null
    private var listView: ListView? = null
    private var localCityName: TextView? = null
    private var adapter: ArrayAdapter<String>? = null
    private val dataList = ArrayList<String>()
    val TAG = "ChooseAreaFragment"

    /*省列表*/
    private var provinceList: List<Province>? = null
    /*市列表*/
    private var cityList: List<City>? = null
    /*县列表*/
    private var countyList: List<County>? = null

    /*选中的省份*/
    private var selectedProvince: Province? = null
    /*选中的城市*/
    private var selectedCity: City? = null
    /*当前选中的级别*/
    var currentLevel: Int = 0
    private var backListener: OnChooseSetBackListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.choose_area_fragment, container, false)
        initWight(view)
        return view
    }

    private fun initWight(view: View) {
        titleText = view.findViewById(R.id.title_text) as TextView
        backButton = view.findViewById(R.id.back_button) as ImageView
        listView = view.findViewById(R.id.list_view) as ListView
        adapter = ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, dataList)
        listView!!.adapter = adapter
        val localCityIcon = view.findViewById(R.id.local_city_icon) as ImageView
        localCityName = view.findViewById(R.id.local_city_name) as TextView
        localCityName!!.setOnClickListener(this)
        val localCityRefresh = view.findViewById(R.id.local_city_refresh) as GifImageView
        GetLocalCityAsyncTask(localCityIcon, localCityName, localCityRefresh).execute()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        queryProvinces()
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList!![position]
                queryCities()
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList!![position]
                queryCounties()
            }else if(currentLevel == LEVEL_COUNTY){
                val stringId = countyList!![position].weatherId
                setResultToWeatherActivity(stringId)
            }
        }
        backButton!!.setOnClickListener(this)
    }

    private fun setResultToWeatherActivity(weatherId: String?) {
        if(activity is MainActivity){
            val intent = Intent(activity, WeatherActivity::class.java)
            intent.putExtra("weather_id", weatherId)
            startActivity(intent)
            activity.finish()
        } else if (activity is LoadFragmentActivity) {
            val intent = Intent()
            intent.putExtra("weather_id", weatherId)
            intent.putExtra("nav_change", 1)
            activity.setResult(1, intent)
            activity.finish()
        }
    }

    fun backToLast() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities()
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces()
        } else {
            activity.finish()
        }
    }

    private fun queryProvinces() {
        titleText!!.text = "中国"
        if (activity is LoadFragmentActivity) {
            backButton!!.visibility = View.VISIBLE
        } else {
            backButton!!.visibility = View.GONE
        }
        provinceList = DataSupport.findAll(Province::class.java)
        if (provinceList!!.size > 0) {
            dataList.clear()
            for (province in provinceList!!) {
                dataList.add(province.provinceName!!)
            }
            adapter!!.notifyDataSetChanged()
            listView!!.setSelection(0)
            currentLevel = LEVEL_PROVINCE
        } else {
            val address = "http://guolin.tech/api/china"
            queryFromServer(address, "province")
        }
    }

    private fun queryCities() {
        titleText!!.text = selectedProvince!!.provinceName
        backButton!!.visibility = View.VISIBLE
        cityList = DataSupport.where("provinceId = ?", selectedProvince!!.provinceCode.toString()).find(City::class.java)
//        LogUtil.e("provinceId",selectedProvince!!.provinceCode.toString())
        if (cityList!!.size > 0) {
            dataList.clear()
            for (city in cityList!!) {
                dataList.add(city.cityName!!)
            }
            adapter!!.notifyDataSetChanged()
            listView!!.setSelection(0)
            currentLevel = LEVEL_CITY
        } else {
            val provinceCode = selectedProvince!!.provinceCode
            val address = "http://guolin.tech/api/china/$provinceCode"
            queryFromServer(address, "city")
        }
    }

    private fun queryCounties() {
        titleText!!.text = selectedCity!!.cityName
        backButton!!.visibility = View.VISIBLE
        countyList = DataSupport.where("cityId = ?", selectedCity!!.cityCode.toString()).find(County::class.java)
        if (countyList!!.size > 0) {
            dataList.clear()
            for (county in countyList!!) {
                dataList.add(county.countyName!!)
            }
            adapter!!.notifyDataSetChanged()
            listView!!.setSelection(0)
            currentLevel = LEVEL_COUNTY
        } else {
            val provinceCode = selectedProvince!!.provinceCode
            val cityCode = selectedCity!!.cityCode
            val address = "http://guolin.tech/api/china/$provinceCode/$cityCode"
            queryFromServer(address, "county")
        }
    }

    private fun queryFromServer(address: String, type: String) {
        showProgressDialog()
        HttpUtil.sendOkHttpRequest(address, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity.runOnUiThread {
                    closeProgressDialog()
                    Toast.makeText(context, "加载失败,请检查网络连接!", Toast.LENGTH_SHORT).show()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body()?.string()
//                LogUtil.e("reponse", responseText)
                var result = false
                if ("province" == type) {
                    result = Utility.handleProvinceRespose(responseText!!)
                } else if ("city" == type) {
                    result = Utility.handleCityResponse(responseText!!, selectedProvince!!.provinceCode)
                } else if ("county" == type) {
                    result = Utility.handleCountResponse(responseText!!, selectedCity!!.cityCode)
                }

                if (result) {
                    activity.runOnUiThread {
                        closeProgressDialog()
                        if ("province" == type) {
                            queryProvinces()
                        } else if ("city" == type) {
                            queryCities()
                        } else if ("county" == type) {
                            queryCounties()
                        }
                    }
                } else {
                    activity.runOnUiThread(Runnable {
                        closeProgressDialog()
                        Toast.makeText(activity,"获取信息失败",Toast.LENGTH_SHORT).show()
                    })
                }
            }
        })
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setMessage("正在加载...")
            progressDialog!!.setCanceledOnTouchOutside(false)
        }
        progressDialog!!.show()
    }

    private fun closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    companion object {
        val LEVEL_PROVINCE = 0
        val LEVEL_CITY = 1
        val LEVEL_COUNTY = 2
    }
}
