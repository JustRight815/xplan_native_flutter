package com.zh.xplan.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.multidex.MultiDex
import com.module.common.interfaces.IApplicationDelegate
import com.zh.sdk.AppConfig
import com.zh.sdk.CommonModule


/**
 * Created by zh on 2017/5/5.
 */
class AppApplication : Application() {

    companion object {
        lateinit var instance: AppApplication
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CommonModule.init(this)
        initModules()
    }

    private fun initModules() {
        for ( moduleApp in AppConfig.moduleApps) {
            try {
                val baseApp:IApplicationDelegate = Class.forName(moduleApp).newInstance() as IApplicationDelegate
                baseApp.onCreate(this)
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onLowMemory() {
        super.onLowMemory()
        for ( moduleApp in AppConfig.moduleApps) {
            try {
                val baseApp:IApplicationDelegate = Class.forName(moduleApp).newInstance() as IApplicationDelegate
                baseApp.onLowMemory()
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f) {
            //非默认值
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources {
        var resources = super.getResources()
        val newConfig = resources.configuration
        val displayMetrics = resources.displayMetrics
        if (newConfig.fontScale != 1f) {
            newConfig.fontScale = 1f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val configurationContext = createConfigurationContext(newConfig)
                resources = configurationContext.resources
                displayMetrics.scaledDensity = displayMetrics.density * newConfig.fontScale
            } else {
                //updateConfiguration 在 API 25(7.0以上系统)之后，被方法 createConfigurationContext 替代
                resources.updateConfiguration(newConfig, displayMetrics)
            }
        }
        return resources
    }

}
