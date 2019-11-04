package com.module.common.interfaces;

import android.app.Application;
import android.content.res.Configuration;

/**
 * 各个组件的Application接口类
 * Created by zh on 2019/11/2.
 */
public interface IApplicationDelegate {

    void onCreate(Application application);

    void onTerminate();

    void onConfigurationChanged(Configuration configuration);

    void onLowMemory();

    void onTrimMemory(int var1);
}
