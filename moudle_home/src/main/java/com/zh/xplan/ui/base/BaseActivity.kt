package com.zh.xplan.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.jaeger.library.StatusBarUtil
import com.module.common.log.LogUtil
import com.zh.swipeback.BaseSwipeBackActivity
import com.zh.xplan.ui.skin.SkinConfigHelper
import org.qcode.qskinloader.IActivitySkinEventHandler
import org.qcode.qskinloader.ISkinActivity
import org.qcode.qskinloader.SkinManager

/**
 * Created by zh on 2017/3/11.
 * Activity 基类 便于统一管理
 */
@SuppressLint("Registered")
open class BaseActivity : BaseSwipeBackActivity(), ISkinActivity {
    val TAG = javaClass.simpleName
    private lateinit var mActivity: Activity
    private lateinit var mSkinEventHandler: IActivitySkinEventHandler//皮肤切换
    private var mFirstTimeApplySkin = true
    var isEnableHideSoftInputFromWindow = true

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivity = this
        if (isSupportSkinChange) {
            initSkinEventHandler()
        }
        super.onCreate(savedInstanceState)
    }

    /**
     * 初始化换肤
     */
    private fun initSkinEventHandler() {
        mSkinEventHandler = SkinManager.newActivitySkinEventHandler()
                .setSwitchSkinImmediately(isSwitchSkinImmediately)
                .setSupportSkinChange(isSupportSkinChange)
                .setWindowBackgroundResource(0)
                .setNeedDelegateViewCreate(true)
        mSkinEventHandler.onCreate(this)
    }

    /**
     * 触摸空白区域自动隐藏键盘
     * @param ev
     * @return
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && isEnableHideSoftInputFromWindow) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        if (isSupportSkinChange) {
            mSkinEventHandler.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
        //皮肤相关，此通知放在此处，尽量让子类的view都添加到view树内
        if (isSupportSkinChange) {
            if (mFirstTimeApplySkin) {
                mSkinEventHandler.onViewCreated()
                mFirstTimeApplySkin = false
            }
            mSkinEventHandler.onResume()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (isSupportSkinChange) {
            mSkinEventHandler.onWindowFocusChanged(hasFocus)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isSupportSkinChange) {
            mSkinEventHandler.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isSupportSkinChange) {
            mSkinEventHandler.onStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isSupportSkinChange) {
            mSkinEventHandler.onDestroy()
        }
    }

    override fun isSupportSkinChange(): Boolean {
        //告知当前界面是否支持换肤：true支持换肤，false不支持
        return false
    }

    //    /**
    //     * true：默认支持所有View换肤，不用添加skin:enable="true"，不想支持则设置skin:enable="false"
    //     * false：默认不支持所有View换肤，对需要换肤的View添加skin:enable="true"
    //     *
    //     * @return
    //     */
    //    @Override
    //    public boolean isSupportAllViewSkin() {
    //        return false;
    //    }

    override fun isSwitchSkinImmediately(): Boolean {
        //告知当切换皮肤时，是否立刻刷新当前界面；true立刻刷新，false表示在界面onResume时刷新；
        //减轻换肤时性能压力
        return false
    }

    override fun handleSkinChange() {
        //当前界面在换肤时收到的回调，可以在此回调内做一些其他事情；
        //比如：通知WebView内的页面切换到夜间模式等
        LogUtil.e("zh", "换肤成功11" + SkinConfigHelper.isDefaultSkin())
    }

    /**
     * 设置状态栏颜色
     * @param color
     */
    protected fun setStatusBarColor(@ColorInt color: Int) {
        setStatusBarColor(color, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA)
    }

    /**
     * 设置状态栏颜色
     * @param color
     * @param statusBarAlpha 透明度
     */
    fun setStatusBarColor(@ColorInt color: Int, @IntRange(from = 0, to = 255) statusBarAlpha: Int) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha)
    }

}
