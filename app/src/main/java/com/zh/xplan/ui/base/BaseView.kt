package com.zh.xplan.ui.base

/**
 * Created by zh on 2017/12/8.
 */
interface BaseView {
    /**
     * 是否显示loading
     * @param isShow true 显示，false不显示
     * @param message
     */
    fun isShowLoading(isShow: Boolean, message: String)

}
