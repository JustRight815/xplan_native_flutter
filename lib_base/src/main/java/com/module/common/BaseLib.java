package com.module.common;

import android.content.Context;
import androidx.annotation.NonNull;
import com.module.common.image.ImageLoader;
import com.module.common.log.LogUtil;
import com.tencent.mmkv.MMKV;

/**
 * Created by zh on 2017/7/31.
 *  BaseLib 工具类初始化相关 主要是为了避免其他util直接使用Application.this造成耦合太大
 */
public final class BaseLib {
    private Context context;
    public static boolean isDebug = true;
    public static String HTTP_HOST = "";

    private BaseLib() {}

    private static class SingleTonHoler{
        private static BaseLib INSTANCE = new BaseLib();
    }

    public static BaseLib getInstance(){
        return SingleTonHoler.INSTANCE;
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public BaseLib init(@NonNull final Context context, boolean isDebug) {
        this.context = context.getApplicationContext();
        BaseLib.isDebug = isDebug;
        LogUtil.init();
        MMKV.initialize(this.context);
        return getInstance();
    }

    /**
     * 获取ApplicationContext
     * @return ApplicationContext
     */
    public Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("u should init first");
    }

    /**
     * 设置OkHtttp网络请求baseUrl
     * @param baseUrl
     * @return
     */
    public  BaseLib setBaseUrl(@NonNull String baseUrl) {
        BaseLib.HTTP_HOST = baseUrl;
        return getInstance();
    }

    /**
     * 初始化imageloader
     * @param context
     * @return
     */
    public BaseLib initImageManager(@NonNull Context context) {
        ImageLoader.init(context);
        return getInstance();
    }

}