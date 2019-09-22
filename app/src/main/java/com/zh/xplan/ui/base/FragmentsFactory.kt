package com.zh.xplan.ui.base

import android.util.SparseArray
import com.zh.xplan.ui.menupicture.PictureFragment
import com.zh.xplan.ui.menusetting.SettingFragment
import com.zh.xplan.ui.menutoutiao.TouTiaoFragment
import com.zh.xplan.ui.menuvideo.VideoFragment
import com.zh.xplan.ui.menuvideo.kaiyanonlinevideo.KaiYanOnlineVideoFragment
import com.zh.xplan.ui.menuvideo.localvideo.LocalVideoFragment

/**
 * Fragments工厂   该工厂的fragment只创建一次
 * Created by zh
 */
object FragmentsFactory {
    val MENU_PICTURE = 0
    val MENU_VIDEO = 2
    val MENU_SETTING = 3
    val ONLINE_VIDEO = 4
    val LOCAL_VIDEO = 5
    val MENU_TOU_TIAO = 7
    // private static Map<Integer, BaseFragment> mFragments = new HashMap<Integer, BaseFragment>();
    // android  APi SparseArray代替HashMap 更为高效
    private val mFragments = SparseArray<BaseFragment>()

    fun createFragment(position: Int): BaseFragment {
        var fragment: BaseFragment? = mFragments.get(position)
        if (fragment == null) {
            when (position) {
                MENU_PICTURE -> fragment = PictureFragment()
                MENU_VIDEO -> fragment = VideoFragment()
                MENU_SETTING -> fragment = SettingFragment()
                ONLINE_VIDEO -> fragment = KaiYanOnlineVideoFragment()
                LOCAL_VIDEO -> fragment = LocalVideoFragment() //
                MENU_TOU_TIAO -> fragment = TouTiaoFragment() //
                else -> {
                    fragment = SettingFragment()
                }
            }
            mFragments.put(position, fragment)
        }
        return fragment
    }

    fun getFragment(position: Int): BaseFragment {
        return mFragments.get(position, null)
    }
}
