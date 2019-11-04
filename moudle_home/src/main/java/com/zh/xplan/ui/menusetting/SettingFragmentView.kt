package com.zh.xplan.ui.menusetting

import androidx.annotation.DrawableRes
import com.zh.xplan.ui.base.BaseView
import com.zh.xplan.ui.weather.model.WeatherBeseModel

/**
 * Created by zh on 2017/12/6.
 */

interface SettingFragmentView : BaseView {

    fun updateCityWeather(weatherBean: WeatherBeseModel.WeatherBean, temperature: String, pm: String, @DrawableRes resid: Int, airCondition: String, cityName: String, weather: String, @DrawableRes weatherRes: Int)
    fun updateCacheSize(cacheSize: String)
}
