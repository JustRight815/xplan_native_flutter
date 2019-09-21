package com.zh.xplan.ui.aboutapp

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import com.google.zxing.WriterException
import com.module.common.utils.PixelUtil
import com.zh.xplan.R
import com.zh.xplan.XPlanApplication
import com.zh.xplan.ui.base.BaseActivity
import com.zh.xplan.ui.zxing.encode.EncodingHandler
import kotlinx.android.synthetic.main.activity_about_app.*
import java.io.UnsupportedEncodingException

/**
 * 关于软件界面  生成二维码实例
 */
class AboutAppActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        initTitle()
        initDatas()
    }

    override fun isSupportSwipeBack(): Boolean {
        return true
    }

    private fun initTitle() {
        title_bar_back.setOnClickListener {
            finish()
        }
        setStatusBarColor(resources.getColor(R.color.colorPrimaryDark), 0)
    }

    fun initDatas() {
        // 6/8 6字体大小为默认的1.3倍
        val text = "X Plan\n       一个收集轮子的demo"
        val end = text.length
        val textSpan = SpannableString(text)
        textSpan.setSpan(RelativeSizeSpan(1.8f), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        textSpan.setSpan(RelativeSizeSpan(1f), 1, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        tv_desc.text = textSpan
        tv_version_name.text = "当前版本号:" + getVersionName()
        createQrCode("X Plan  一个随意的demo", 170f)
    }

    /**
     * 生成二维码
     * @param key
     */
    private fun createQrCode(key: String, size: Float): Bitmap? {
        var qrCode: Bitmap? = null
        try {
            qrCode = EncodingHandler.create2Code(key, PixelUtil.dp2px(size, this))
            iv_qr_code.setImageBitmap(qrCode)
        } catch (e: WriterException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return qrCode
    }

    private fun getVersionName(): String {
        var versionName = ""
        try {
            val pm = XPlanApplication.instance.packageManager
            val packageInfo: PackageInfo? = pm.getPackageInfo(XPlanApplication.instance.packageName,
                    PackageManager.GET_CONFIGURATIONS)
            versionName = packageInfo?.versionName?:""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionName
    }
}
