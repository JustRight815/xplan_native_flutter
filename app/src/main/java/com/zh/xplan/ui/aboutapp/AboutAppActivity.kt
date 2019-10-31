package com.zh.xplan.ui.aboutapp

import android.Manifest
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.zxing.WriterException
import com.module.common.utils.PixelUtil
import com.module.common.view.snackbar.SnackbarUtils
import com.zh.xplan.R
import com.zh.xplan.XPlanApplication
import com.zh.xplan.ui.base.BaseActivity
import com.zh.xplan.common.utils.ImageUtils
import com.zh.xplan.ui.zxing.activity.CaptureActivity.getAppDetailSettingIntent
import com.zh.xplan.ui.zxing.encode.EncodingHandler
import kotlinx.android.synthetic.main.activity_about_app.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 关于软件界面  生成二维码实例
 */
@RuntimePermissions
class AboutAppActivity : BaseActivity() {
    private lateinit var mContentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentView = View.inflate(this, R.layout.activity_about_app, null)
        setContentView(mContentView)
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

    private fun initDatas() {
        // 6/8 6字体大小为默认的1.3倍
        val text = "X Plan\n       一个收集轮子的demo"
        val end = text.length
        val textSpan = SpannableString(text)
        textSpan.setSpan(RelativeSizeSpan(1.8f), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        textSpan.setSpan(RelativeSizeSpan(1f), 1, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        tv_desc.text = textSpan
        tv_version_name.text = "当前版本号:" + getVersionName()
        createQrCode("X Plan  一个随意的demo", 170f)

        btn_save.setOnClickListener {
            saveImageWithPermissionCheck()
        }
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

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun saveImage() {
        val qrCodeView: View = findViewById(R.id.ll_qr_code_layout)
        val bitmap = ImageUtils.view2Bitmap(qrCodeView)
        if (bitmap == null) {
            SnackbarUtils.ShortToast(mContentView, "保存失败")
        } else {
            val fileName = getFileName()
            val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    fileName
            )
            val isSaveSuccess = ImageUtils.saveAndScanMedia(
                    this@AboutAppActivity,
                    bitmap,
                    file,
                    Bitmap.CompressFormat.PNG,
                    true
            )
            if (isSaveSuccess) {
                SnackbarUtils.ShortToast(mContentView, "保存成功")
            } else {
                SnackbarUtils.ShortToast(mContentView, "保存失败")
            }
        }
    }

    /**
     * 获取文件名
     * @return
     */
    private fun getFileName(): String {
        return "XPlan-" +  SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(Date(System.currentTimeMillis())) + "-" + System.currentTimeMillis() + ".png"
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showRecordDenied() {
        SnackbarUtils.ShortToast(mContentView, "拒绝存储权限将无法保存图片")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun onRecordNeverAskAgain() {
        AlertDialog.Builder(this)
                .setPositiveButton("好的") { dialog, which ->
                    //打开系统设置权限
                    val intent = getAppDetailSettingIntent(this@AboutAppActivity)
                    startActivity(intent)
                    dialog.cancel()
                }
                .setNegativeButton("取消") { dialog, which -> dialog.cancel() }
                .setCancelable(false)
                .setMessage("您已经禁止了存储权限,是否去开启权限")
                .show()
    }
}
