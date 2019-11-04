package com.zh.xplan;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.template.IProvider;

@Route(path = "/main/MainModule", name = "模块初始化")
public class MainModule implements IProvider {
    private static final String TAG = "MainModule";
    @Override
    public void init(Context context) {
        Log.e(TAG, "MainModule调用了初始化" );
    }
}
