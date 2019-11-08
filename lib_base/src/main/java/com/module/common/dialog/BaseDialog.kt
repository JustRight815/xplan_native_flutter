package com.module.common.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.module.common.R
import com.module.common.utils.ScreenUtil

open class BaseDialog(context: Context, private val mBuilder: DefaultBuilder? = null, themeResId: Int = R.style.DialogCommonStyle) : Dialog(context, themeResId) {
    private var mView: View? = null
    private var mDefaultPaddingLeftRight = 90f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        var view = getCustomView()
        if (view == null) {
            view = getDefaultView()
        }
        setContentView(view)
    }

    private fun getDefaultView(): View {
        return LayoutInflater.from(context.applicationContext).inflate(R.layout.base_dialog_common_layout, null, false).apply {
            findViewById<TextView>(R.id.base_dialog_title).apply {
                val title = mBuilder?.defaultTitle
                if (!TextUtils.isEmpty(title)) {
                    visibility = View.VISIBLE
                    text = title
                } else {
                    visibility = View.GONE
                }
            }
            findViewById<TextView>(R.id.base_dialog_text).apply {
                val contentStr = mBuilder?.contentStr
                if (!TextUtils.isEmpty(contentStr)) {
                    text = contentStr
                }
            }
            findViewById<Button>(R.id.base_dialog_negative_btn).apply {
                val btText = mBuilder?.negativeBtnStr
                text = btText
                setOnClickListener {
                    mBuilder?.negativeBtnOperate?.invoke(this@BaseDialog) ?: dismiss()
                }
            }
            findViewById<Button>(R.id.base_dialog_positive_btn).apply {
                val btText = mBuilder?.positiveBtnStr
                text = btText
                setOnClickListener {
                    mBuilder?.positiveBtnOperate?.invoke(this@BaseDialog) ?: dismiss()
                }
            }
        }
    }

    fun getCustomView(): View? {
        return mView
    }

    fun setCustomView(view: View) {
        mView = view
    }

    override fun onStart() {
        super.onStart()
        val lp = window?.attributes
        getLayoutParams(lp)
        window?.attributes = lp
    }

    override fun show() {
        try {
            if (!isShowing) {
                super.show()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            if (isShowing) {
                super.dismiss()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    open fun getGravity(): Int = Gravity.CENTER

    open fun getLayoutParams(lp: WindowManager.LayoutParams?) {
        if (mBuilder == null) {
            if (getWidth() != -1f) {
                lp?.width = ScreenUtil.dip2px(context, getWidth() - 2 * getHorPadding())
            } else {
                lp?.width = ScreenUtil.getScreenWidth(context) - 2 * getHorPadding().toInt()
            }
        } else {
            lp?.width = ScreenUtil.getScreenWidth(context) - ScreenUtil.dip2px(context, if (mBuilder.defaultLeftRightPadding == -1f) mDefaultPaddingLeftRight else mBuilder.defaultLeftRightPadding)
        }
        lp?.gravity = getGravity()
    }

    open fun getWidth() = -1f

    open fun getHorPadding() = 0f

    open class DefaultBuilder(var context: Context) {
        var defaultTitle: String? = null
        var contentStr: String? = null
        var negativeBtnStr: String? = null
        var negativeBtnOperate: ((dialog: Dialog?) -> Unit)? = null
        var positiveBtnStr: String? = null
        var positiveBtnOperate: ((dialog: Dialog?) -> Unit)? = null
        var defaultLeftRightPadding: Float = -1f

        fun setDefaultTitle(defaultTitle: String?): DefaultBuilder {
            this.defaultTitle = defaultTitle
            return this
        }

        fun setContextText(contentStr: String): DefaultBuilder {
            this.contentStr = contentStr
            return this
        }

        open fun setNegativeButton(btText: String, operate: ((dialog: Dialog?) -> Unit)? = null): DefaultBuilder {
            negativeBtnStr = btText
            negativeBtnOperate = operate
            return this
        }

        open fun setPositiveButton(
                btText: String,
                operate: ((dialog: Dialog?) -> Unit)? = null): DefaultBuilder {
            positiveBtnStr = btText
            positiveBtnOperate = operate
            return this
        }

        open fun setDefalutLeftRightPadding(leftRightPadding: Float): DefaultBuilder {
            defaultLeftRightPadding = leftRightPadding
            return this
        }

        fun builder() = BaseDialog(context, this)
    }
}