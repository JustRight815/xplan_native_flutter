package com.zh.xplan.ui.indexactivity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import com.module.common.log.LogUtil
import com.zh.xplan.XPlanApplication
import com.zh.xplan.ui.base.BaseActivity
import com.zh.xplan.ui.mainactivity.MainActivity
import com.zh.xplan.common.utils.CustomDensityUtil
import com.zh.xplan.common.utils.FileUtils
import java.io.File

/**
 * 启动界面
 */
class IndexActivity : BaseActivity() {
    private val SPLASH_FILE_NAME = "splash"

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    override fun isSupportSkinChange(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        swipeBackEnable = false
        CustomDensityUtil.setCustomDensity(this, XPlanApplication.instance)
    }

    override fun onResume() {
        super.onResume()
        jumpActivity()
    }

    private fun jumpActivity() {
        // 跳转到主界面
        val splashImg = File(FileUtils.getSplashDir(), SPLASH_FILE_NAME)
        if (splashImg.exists()) {
            LogUtil.e("zh", "splashImg.exists() ")
            val bitmap = BitmapFactory.decodeFile(splashImg.path)
            if (bitmap != null) {
                LogUtil.e("zh", "FileUtils.bitmap ")
                startActivity(Intent(this, AdActivity::class.java))
                finish()
                return
            }
        }
        LogUtil.e("zh", "IndexActivity splashImg not exists() ")
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }

}
