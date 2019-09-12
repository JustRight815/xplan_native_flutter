package com.zh.xplan.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.module.common.log.LogUtil
import com.module.common.utils.PixelUtil
import com.zh.xplan.R
import com.zh.xplan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_webview.*

/**
 * 简单的加载web的页面示例
 * @author zh
 */
class NativeWebViewActivity : BaseActivity() {
    private var mWebView: WebView? = null
    private var mURL: String? = null
    private var currentProgress = 0
    private var mTitle = "" //页面标题
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        setStatusBarColor(resources.getColor(R.color.colorPrimaryDark), 0)
        initView()
        initData()
        loadURL()
    }


    private fun initView() {
        title_bar_back.setOnClickListener {
            finish()
        }
        ProgressBar.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, PixelUtil.dp2px(2f, this))
        //防止内存泄露
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mWebView = PreloadWebView.instance.getWebView(this)
        mWebView?.let {
            it.layoutParams = params
            ll_webview_layout.addView(it)
            initWebView(it)
        }
    }

    private fun initData() {
        val intent = intent
        mURL = intent.getStringExtra("URL")
    }

    private fun loadURL() {
        LogUtil.e("zh", "mURL$mURL")
        mWebView?.let { webView ->
            mURL?.let {
                webView.loadUrl(it)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    private fun initWebView(webView: WebView) {
        webView.webChromeClient = object : WebChromeClient() {
            // 监听网页进度
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress > currentProgress) {
                    currentProgress = newProgress
                    ProgressBar.progress = newProgress
                }
                if (newProgress == 100) {
                    val h = Handler()
                    h.postDelayed({
                        ProgressBar.visibility = View.GONE
                        currentProgress = 0
                    }, 500)
                } else {
                    if (ProgressBar.visibility == View.GONE) {
                        ProgressBar.visibility = View.VISIBLE
                    }
                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                //获取到网页标题
                mTitle = title
                title_name.text = mTitle
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        mWebView?.let {
            if (keyCode == KeyEvent.KEYCODE_BACK && it.canGoBack()) {
                it.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    //在 Activity销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空。
    override fun onDestroy() {
        mWebView?.let {
            it.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            it.clearHistory()
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再destory()
            val parent = it.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(it)
            }
            it.stopLoading()
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            it.settings.javaScriptEnabled = false
            it.removeAllViews()
            it.destroy()
            mWebView = null
        }
        PreloadWebView.instance.preload()
        super.onDestroy()
    }
}
