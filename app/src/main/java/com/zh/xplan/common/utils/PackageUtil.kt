package com.zh.xplan.common.utils

import android.content.Context
import android.os.Process
import android.text.TextUtils
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

object PackageUtil {
    /**
     * 当前进程是否是主线程
     * @return
     */
    fun isMainProcess(context: Context): Boolean {
        val packageName = context.packageName
        val processName = getProcessName(Process.myPid())
        return packageName != null && packageName == processName
    }

    /**
     * 获取进程号对应的进程名 bugly推荐
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }
}