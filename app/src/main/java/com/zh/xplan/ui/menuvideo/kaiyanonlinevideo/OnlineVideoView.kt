package com.zh.xplan.ui.menuvideo.kaiyanonlinevideo

import com.zh.xplan.ui.base.BaseView

/**
 * Created by zh on 2017/12/6.
 */

interface OnlineVideoView : BaseView {

    fun updateOnlineData(isSuccess: Boolean, response: String, isPullDownRefresh: Boolean?)
}
