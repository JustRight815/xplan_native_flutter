package com.zh.xplan.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.module.common.net.FileUtil
import java.io.File

/**
 * 安装apk
 */
object InstallApkUtil {

    fun autoInstallApk(context: Context,file: File?) {
        if (file == null) {
            return
        }
        if (FileUtil.getExtension(file.path) == "apk") {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri: Uri?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //还需要权限 android.permission.REQUEST_INSTALL_PACKAGES
                uri = FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", file)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }else{
                uri = Uri.fromFile(file)
            }
            uri?.let {
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
                context.startActivity(intent)
            }
        }
    }
}