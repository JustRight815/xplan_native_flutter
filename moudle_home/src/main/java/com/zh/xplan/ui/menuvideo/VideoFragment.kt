package com.zh.xplan.ui.menuvideo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import com.zh.xplan.R
import com.zh.xplan.ui.base.BaseFragment
import com.zh.xplan.ui.base.FragmentsFactory
import com.zh.xplan.ui.camera.RecordVideoSet
import com.zh.xplan.ui.camera.record.CustomCameraActivity
import org.qcode.qskinloader.SkinManager

/**
 * 视频菜单  Created by zh
 */
class VideoFragment : BaseFragment() {
    private var mFragmentManager: FragmentManager? = null
    private var mOnlineVideoBtn: RadioButton? = null
    private var mLocalVideoBtn: RadioButton? = null
    private var mCameraView: ImageView? = null
    private var mViewPager: ViewPager? = null

    //控制当前界面是否可以滑动返回到上一界面
    private var OnPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            if (position == 0) {
                if(mOnlineVideoBtn?.isChecked == false){
                    mOnlineVideoBtn?.isChecked  = true
                }
            } else {
                if(mLocalVideoBtn?.isChecked == false){
                    mLocalVideoBtn?.isChecked  = true
                }
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val mView = LayoutInflater.from(activity).inflate(R.layout.fragment_video, null)
        initView(mView)
        initDatas()
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SkinManager.getInstance().applySkin(view, true)
    }

    /**
     * 初始化views
     *
     * @param mView
     */
    private fun initView(mView: View) {
        mViewPager = mView.findViewById<View>(R.id.viewPager) as ViewPager
        mOnlineVideoBtn = mView.findViewById<View>(R.id.rb_online_video) as RadioButton
        mLocalVideoBtn = mView.findViewById<View>(R.id.rb_local_video) as RadioButton
        mCameraView = mView.findViewById<View>(R.id.iv_camera) as ImageView
        mOnlineVideoBtn?.setOnCheckedChangeListener { arg0, isChecked ->
            if (isChecked) {
                mViewPager?.currentItem = 0
            }
        }
        mLocalVideoBtn?.setOnCheckedChangeListener { arg0, isChecked ->
            if (isChecked) {
                mViewPager?.currentItem = 1
            }
        }
        mCameraView?.setOnClickListener { customRecord(true) }

    }

    private fun initDatas() {
        activity?.let {
            mFragmentManager = it.supportFragmentManager
            mFragmentManager?.let { fragmentManager ->
                mOnlineVideoBtn?.isChecked = true
                mViewPager?.adapter = FragmentPagerAdapter(fragmentManager)
                mViewPager?.addOnPageChangeListener(OnPageChangeListener)
            }
        }
    }

    private class FragmentPagerAdapter(fragmentManager: FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            return if (position == 0) {
                FragmentsFactory.createFragment(4)
            } else {
                FragmentsFactory.createFragment(5)
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }

    /**
     * 启用自定义相机录制视频
     * @param isSmallVideo
     */
    fun customRecord(isSmallVideo: Boolean) {
        val recordVideoSet = RecordVideoSet()
        recordVideoSet.limitRecordTime = 30
        recordVideoSet.isSmallVideo = isSmallVideo
        val intent = Intent(activity, CustomCameraActivity::class.java)
        intent.putExtra("RECORD_VIDEO_CONFIG", recordVideoSet)
        startActivity(intent)
    }

}