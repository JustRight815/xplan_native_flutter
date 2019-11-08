package com.module.common.dialog

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.module.common.R
import com.module.common.utils.ScreenUtil

object DialogUtil{

    fun getLoadingDialog(activity: Activity): Dialog {
        val inflater = LayoutInflater.from(activity)
        val v = inflater.inflate( R.layout.base_dialog_loading_layout, null)
        val loadingDialog = Dialog(activity,  R.style.DialogCommonLoadingStyle)
        loadingDialog.setCancelable(false)
        loadingDialog.setContentView(v, LinearLayout.LayoutParams(
                ScreenUtil.dip2px(activity, 80f), ScreenUtil.dip2px(activity, 80f)))
        return loadingDialog
    }
}
