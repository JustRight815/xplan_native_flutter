package com.zh.xplan

import android.app.Application
import android.content.res.Configuration
import android.os.Process
import android.util.Log
import com.bumptech.glide.Glide
import com.module.common.BaseLib
import com.module.common.interfaces.IApplicationDelegate
import com.module.common.log.LogUtil
import com.module.common.utils.CrashUtils
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.smtt.sdk.QbSdk
import com.zh.swipeback.SlideFinishManager
import com.zh.xplan.common.utils.PackageUtil
import com.zh.xplan.ui.menupicture.utils.ScreenUtil
import com.zh.xplan.ui.skin.Settings
import com.zh.xplan.ui.skin.SkinChangeHelper
import com.zh.xplan.ui.webview.PreloadWebView
import org.litepal.LitePal
import kotlin.system.exitProcess

/**
 * Created by zh on 2017/5/5.
 */

class XPlanApplication : IApplicationDelegate {

    companion object {
        lateinit var instance: Application

        fun destroyApp() {
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    override fun onCreate(application: Application) {
        instance = application
        Log.e("zh1","XPlanApplication1 onCreate ===========")
        delayInit()
    }

    private fun delayInit() {
        if (PackageUtil.isMainProcess(instance)) {
            CrashUtils.init()
            CrashReport.initCrashReport(instance, "0785b70a94", AppConstants.isDebug)
            BaseLib.getInstance().init(instance, AppConstants.isDebug)
                    .setBaseUrl(AppConstants.HTTP_HOST)
                    .initImageManager(instance)
            initSkinLoader()
            SlideFinishManager.getInstance().init(instance)
            //初始化litePal
            LitePal.initialize(instance)
            initX5QbSdk()
            Glide.get(instance)
            ScreenUtil.init(instance)
            PreloadWebView.instance.preload()
        }
    }

    private fun initX5QbSdk() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //x5内核初始化接口
        QbSdk.initX5Environment(instance, object : QbSdk.PreInitCallback {
            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtil.e("application", "X5QbSdk onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {}
        })
        if (!QbSdk.isTbsCoreInited()) {
            // preinit只需要调用一次，如果已经完成了初始化，那么就直接构造view
            LogUtil.e("application", "预加载中...preinitX5WebCore")
            QbSdk.preInit(instance, null)
        }
    }



    /**
     * Must call init first
     */
    private fun initSkinLoader() {
        Settings.createInstance(instance)
        // 初始化皮肤框架
        SkinChangeHelper.getInstance().init(instance)
        //初始化上次缓存的皮肤
        SkinChangeHelper.getInstance().refreshSkin(null)
    }

    override fun onLowMemory() {
        Glide.get(instance).clearMemory()
    }

    override fun onTerminate() {
    }


    override fun onTrimMemory(var1: Int) {
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {

    }
}
