package com.zh.sdk;

import android.app.Application;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 *
 */
public class CommonModule {
    public static Application instance;

    public static void init(Application application){
        instance = application;
        if(BuildConfig.DEBUG){
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application);
        //平行module初始化
        ARouter.getInstance().build("/main/MainModule").navigation();
    }
}
