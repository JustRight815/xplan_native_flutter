package com.zh.xplan

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Process
import androidx.multidex.MultiDex
import com.bumptech.glide.Glide
import com.module.common.BaseLib
import com.module.common.log.LogUtil
import com.module.common.utils.CrashUtils
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.smtt.sdk.QbSdk
import com.zh.swipeback.SlideFinishManager
import com.zh.xplan.ui.menupicture.utils.ScreenUtil
import com.zh.xplan.ui.skin.Settings
import com.zh.xplan.ui.skin.SkinChangeHelper
import com.zh.xplan.ui.utils.PackageUtil
import com.zh.xplan.ui.webview.PreloadWebView
import org.litepal.LitePal
import kotlin.system.exitProcess

/**
 * Created by zh on 2017/5/5.
 */
class XPlanApplication : Application() {
    companion object {
        lateinit var instance: XPlanApplication
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        delayInit()
    }

    private fun delayInit() {
        if (PackageUtil.isMainProcess(instance)) {
            CrashUtils.init()
            CrashReport.initCrashReport(applicationContext, "0785b70a94", AppConstants.isDebug)
            BaseLib.getInstance().init(this, AppConstants.isDebug)
                    .setBaseUrl(AppConstants.HTTP_HOST)
                    .initImageManager(this)
            initSkinLoader()
            SlideFinishManager.getInstance().init(this)
            //初始化litePal
            LitePal.initialize(applicationContext)
            initX5QbSdk()
            Glide.get(this)
            ScreenUtil.init(this)
            PreloadWebView.instance.preload()
        }
    }

    private fun initX5QbSdk() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //x5内核初始化接口
        QbSdk.initX5Environment(applicationContext, object : QbSdk.PreInitCallback {
            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtil.e("application", "X5QbSdk onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {}
        })
        if (!QbSdk.isTbsCoreInited()) {
            // preinit只需要调用一次，如果已经完成了初始化，那么就直接构造view
            LogUtil.e("application", "预加载中...preinitX5WebCore")
            QbSdk.preInit(applicationContext, null)
        }
    }

    fun destroyApp() {
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    /**
     * Must call init first
     */
    private fun initSkinLoader() {
        Settings.createInstance(this)
        // 初始化皮肤框架
        SkinChangeHelper.getInstance().init(this)
        //初始化上次缓存的皮肤
        SkinChangeHelper.getInstance().refreshSkin(null)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    //设置app字体不允许随系统调节而发生大小变化
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
