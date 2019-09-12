package com.zh.xplan.ui.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.jaeger.library.StatusBarUtil
import com.module.common.log.LogUtil
import com.module.common.view.bottombar.BottomBarTextColorAttrHandler
import com.module.common.view.snackbar.SnackbarUtils
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.zh.xplan.R
import com.zh.xplan.XPlanApplication
import com.zh.xplan.ui.aboutapp.AboutAppActivity
import com.zh.xplan.ui.base.BaseActivity
import com.zh.xplan.ui.base.BaseFragment
import com.zh.xplan.ui.base.FragmentsFactory
import com.zh.xplan.ui.citypicker.CityPickerActivity
import com.zh.xplan.ui.iptoolsactivity.IpToolsActivity
import com.zh.xplan.ui.menuvideo.kaiyanonlinevideo.KaiYanOnlineVideoFragment
import com.zh.xplan.ui.pulltorefreshdemo.PullToRefreshDemoActivity
import com.zh.xplan.ui.skin.SkinChangeHelper
import com.zh.xplan.ui.skin.SkinConfigHelper
import com.zh.xplan.ui.webview.X5WebViewActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.activity_mian_content.*
import org.qcode.qskinloader.SkinManager

/**
 * 主界面
 */
class MainActivity : BaseActivity(), View.OnClickListener, MainView {
    private var mExitTime: Long = 0 // 延时退出应用时间变量
    //头部布局
    private var cityName: String? = null
    private var mainPresenter: MainPresenter? = null
    private var mFragmentManager: FragmentManager? = null
    private var mCurrentFragment: BaseFragment? = null// 当前FrameLayout中显示的Fragment
    private var isFirst: Boolean = true// 是否是第一次进入应用


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SkinManager.getInstance().registerSkinAttrHandler(
                BottomBarTextColorAttrHandler.BOTTOM_BAR_TEXT_COLOR, BottomBarTextColorAttrHandler())
        setContentView(R.layout.activity_main)
        swipeBackEnable = false
        mFragmentManager = supportFragmentManager
        initViews()
        initDatas()
    }

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    override fun isSupportSkinChange(): Boolean {
        return true
    }

    override fun isSwitchSkinImmediately(): Boolean {
        return true
    }

    private fun initViews() {
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this@MainActivity, drawerLayout, resources.getColor(R.color.colorPrimaryDark))
        header_tv_city_name.setOnClickListener(this)
        header_tv_temperature.setOnClickListener(this)
        tv_temperature_point.setOnClickListener(this)
        ll_city_layout.setOnClickListener(this)
        ll_refresh.setOnClickListener(this)
        ll_about.setOnClickListener(this)
        ll_ip_tools.setOnClickListener(this)
        ll_night_mode.setOnClickListener(this)
        initBottomMenus()
        changeNightModeSwitch()
        night_mode_switch.setOnCheckedChangeListener { _, isChecked ->
            if (SkinConfigHelper.isDefaultSkin() == isChecked) {
                LogUtil.e("zh", "onCheckedChanged  $isChecked")
                SkinChangeHelper.getInstance().switchSkinMode(
                        object : SkinChangeHelper.OnSkinChangeListener {
                            override fun onSuccess() {
                                LogUtil.e("zh", "换肤成功")
                                changeStatusBarColor()
                            }

                            override fun onError() {
                                LogUtil.e("zh", "换肤失败")
                            }
                        }
                )
            }
        }
    }

    /**
     * 初始化底部菜单的图片。（也可以扩展动态更新菜单文字）
     */
    private fun initBottomMenus() {
        bottom_bar.init(null)
        bottom_bar.setOnItemSelectedListener { _, position ->
            GSYVideoManager.releaseAllVideos()
            when (position) {
                0 -> {
                    changeStatusBarColor()
                    setFragment(mCurrentFragment,
                            FragmentsFactory.createFragment(FragmentsFactory.MENU_PICTURE))
                }
                1 -> {
                    changeStatusBarColor()
                    setFragment(mCurrentFragment,
                            FragmentsFactory.createFragment(FragmentsFactory.MENU_VIDEO))
                }
                2 -> {
                    changeStatusBarColor()
                    setFragment(mCurrentFragment,
                            FragmentsFactory.createFragment(FragmentsFactory.MENU_TOU_TIAO))
                }
                3 -> {
                    changeStatusBarColor()
                    setFragment(mCurrentFragment,
                            FragmentsFactory.createFragment(FragmentsFactory.MENU_SETTING))
                }
                else -> {
                }
            }
        }
        // 设置默认选中第一个菜单
        //        mBottomBar.getBottomItem(0).performClick();
        bottom_bar.currentItem = 0
        bottom_bar.setMsg(3, "NEW")//设置第四个页签显示NEW提示文字
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.header_tv_temperature// 打开web天气预报
            -> {
                val intent = Intent(this,
                        X5WebViewActivity::class.java)
                intent.putExtra("URL", "http://www.uc123.com/tianqi.html")
                startActivity(intent)
            }
            R.id.tv_temperature_point// 打开web天气预报
            -> {
                val intent1 = Intent(this,
                        X5WebViewActivity::class.java)
                intent1.putExtra("URL", "http://www.uc123.com/tianqi.html")
                startActivity(intent1)
            }
            R.id.header_tv_city_name// 城市选择
            -> {
                val cityIntent = Intent(this, CityPickerActivity::class.java)
                startActivityForResult(cityIntent, 0)
            }
            R.id.ll_refresh// 下拉刷新
            -> startActivity(Intent(this, PullToRefreshDemoActivity::class.java))
            R.id.ll_about// 关于软件
            -> startActivity(Intent(this, AboutAppActivity::class.java))
            R.id.ll_ip_tools// ip工具
            -> startActivity(Intent(this, IpToolsActivity::class.java))
            R.id.ll_night_mode// 夜间模式
            -> SkinChangeHelper.getInstance().switchSkinMode(
                    object : SkinChangeHelper.OnSkinChangeListener {
                        override fun onSuccess() {
                            LogUtil.e(TAG, "换肤成功")
                            changeStatusBarColor()
                            changeNightModeSwitch()
                        }

                        override fun onError() {
                            //                            LogUtil.e(TAG,"换肤失败");
                        }
                    }
            )
            else -> {
            }
        }
    }

    fun closeDrawerDelay() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.postDelayed({ drawerLayout!!.closeDrawer(GravityCompat.START) }, 320)
        }
    }

    fun closeDrawer() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    fun openDrawer() {
        drawerLayout?.openDrawer(GravityCompat.START)
    }

    fun initDatas() {
        mainPresenter = MainPresenter()
        mainPresenter?.apply {
            attachView(this@MainActivity)
            getCityWeather("", "")
            updateAdPicture()
        }
    }

    private fun changeStatusBarColor() {
        var color = if (SkinConfigHelper.isDefaultSkin()) resources.getColor(R.color.colorPrimaryDark) else resources.getColor(R.color.colorPrimaryDark_night)
        if (bottom_bar.currentItem == 2) {
            color = if (SkinConfigHelper.isDefaultSkin()) resources.getColor(R.color.btn_pressed_grey_solid) else resources.getColor(R.color.colorPrimaryDark_night)
        }
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this@MainActivity, drawerLayout, color)
    }

    private fun changeNightModeSwitch() {
        night_mode_switch.isChecked = !SkinConfigHelper.isDefaultSkin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == 66 && data != null) {
            val cityName = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY)
            if (cityName != null && header_tv_city_name != null) {
                if (cityName != this.cityName) {
                    this.cityName = cityName
                    header_tv_city_name.text = cityName
                    mainPresenter?.getCityWeather(null, cityName)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showCityWeather(cityName: String, temperature: String) {
        header_tv_city_name.text = cityName
        header_tv_temperature.text = temperature
    }

    override fun isShowLoading(isShow: Boolean, message: String) {

    }

    /**
     * 切换显示不同的fragment
     */
    private fun setFragment(fromFragment: Fragment?, toFragment: Fragment) {
        mFragmentManager?.let {
            val transaction = it.beginTransaction()
            if (isFirst) {
                // 如果是第一次进入应用，把菜单1对应的fragment加载进去，并显示
                transaction.add(R.id.id_content, toFragment).commit()
                mCurrentFragment = toFragment as BaseFragment
                isFirst = false
                return
            }
            if (mCurrentFragment !== toFragment) {
                // 隐藏之前的fragment,显示下一个fragment
                mCurrentFragment = toFragment as BaseFragment
                fromFragment?.let { fromFragment ->
                    if (!toFragment.isAdded) {
                        transaction.hide(fromFragment).add(R.id.id_content, toFragment).commit()
                    } else {
                        transaction.hide(fromFragment).show(toFragment).commit()
                    }
                }
            }
        }
    }


    /**
     * 按两下返回键退出程序
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //如果侧滑菜单是打开的，则关闭
            val drawer = findViewById<View>(R.id.drawerLayout) as DrawerLayout
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
                return true
            }

            //如果正在全屏播放视频则关闭全屏视频，不提示退出程序
            val curFragment = FragmentsFactory.getFragment(4)
            if (curFragment is KaiYanOnlineVideoFragment) {
                LogUtil.e("zh", "curFragment  instanceof ")
                if (curFragment.onBackPressed()) {
                    LogUtil.e("zh", "curFragment  onBackPressed ")
                    return true
                }
            }

            if (System.currentTimeMillis() - mExitTime > 2000) {
                // 自定义Toast的样式
                //                Toast toast = new Toast(this);
                //                View view = LayoutInflater.from(this).inflate(
                //                        R.layout.toast_exit_app, null);
                //                TextView textView = (TextView) view
                //                        .findViewById(R.id.tv_exit_toast);
                //                textView.setText("再按一次退出程序");
                //                toast.setView(view);
                //                toast.setDuration(Toast.LENGTH_SHORT);
                //                toast.show();
                // 防止通知栏关闭后Toast不提示
                SnackbarUtils.ShortToast(bottom_bar, "再按一次退出程序")
                mExitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        mainPresenter?.onDestory()
        XPlanApplication.getInstance().destroyApp()
        super.onDestroy()
    }
}
